package org.basalt.ewallet;

import org.basalt.ewallet.messageclasses.CreateAccountResp;
import org.basalt.ewallet.messageclasses.CreateAccountReq;
import io.javalin.Javalin;
import io.javalin.http.Context;
import io.javalin.http.Handler;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import org.basalt.ewallet.dataclasses.EWSession;
import org.basalt.ewallet.dataclasses.EWTransaction;
import org.basalt.ewallet.dataclasses.EWUser;
import org.basalt.ewallet.messageclasses.LoginReq;
import org.basalt.ewallet.messageclasses.LoginResp;
import org.basalt.ewallet.messageclasses.LogoutReq;
import org.basalt.ewallet.messageclasses.LogoutResp;
import org.basalt.ewallet.messageclasses.TransactionReq;
import org.eclipse.jetty.util.security.Credential;

public class EWallet {
    
    Javalin javalin;
    DataLayer dataLayer;
    
    public EWallet( DataLayer dataLayer ) {
        this.dataLayer = dataLayer;
    }
    
    public String hashPassword( String password, String username ) {
        
        String passAndSalt = password + ":" + username;
        System.out.println( "MD5=" + Credential.MD5.digest(passAndSalt));
        return( Credential.MD5.digest(passAndSalt).substring(4 ) );
        
    }
    
    public void init() {
        javalin = Javalin.create(config -> {
            config.plugins.enableCors(cors -> {
                cors.add(it -> {
                    it.reflectClientOrigin = true;
                });
            });
        });
        
        javalin.post( "/createAccount", createAccount() );
        javalin.post( "/login", doLogin() );
        javalin.post( "/logout", doLogout() );
        javalin.post( "/sec/showTransactions", showTransactions() );
        javalin.post( "/sec/showBalance", showBalance() );
        javalin.post( "/sec/doCredit", doCredit() );
        javalin.post( "/sec/doDebit", showTransactions() );
        javalin.before( "/sec/*", securityHandler() );
        javalin.start( 8000 );
    }
    
    public Handler securityHandler() {
        return( new Handler() {
            @Override
            public void handle(Context ctx) throws Exception {
                Map<String,String> headers = ctx.headerMap();
                Set<String> keys = headers.keySet();
                for ( String key : keys ) {
                    System.out.println( key + " => " + headers.get( key ) );
                }
            }
            
        });
    }
    
    public Handler createAccount() {
        return( new Handler() {
            
            @Override
            public void handle(Context ctx) throws Exception {
                CreateAccountReq req = ctx.bodyAsClass(CreateAccountReq.class );
                String sql = "insert into ew_user ( username, passhash ) "
                           + "values ( ?, ? )";
                Long userId = dataLayer.insertWithId(sql, req.getUsername(),  hashPassword(req.getPassword(),req.getUsername() ) );
                //if ( userId >)
                ctx.json( new CreateAccountResp( userId ) );
            }
        });
    }
    
    public Handler doLogin() {
        return( new Handler() {
            @Override
            public void handle(Context ctx) throws Exception {
                LoginReq req = ctx.bodyAsClass( LoginReq.class );
                LoginResp resp = new LoginResp();
                
                String sql = "select * from ew_user "
                           + "where ( username = ? ) "
                           + "  and ( passhash = ? ) ";
                List<EWUser> userList = dataLayer.query(sql, EWUser.class, req.getUsername(), hashPassword(req.getPassword(),req.getUsername() ) );
                if ( !userList.isEmpty() ) {
                    UUID uuid = UUID.randomUUID();
                    EWUser user = userList.get( 0 );
                    sql = "with ew_dead_sessions as ( " 
                        + "    update ew_session " 
                        + "    set expiry_date = CURRENT_TIMESTAMP " 
                        + "    where ( user_id = ? ) "
                        + "      and ( current_timestamp < expiry_date ) "
                        + "    returning * " 
                        + "), " 
                        + "ew_new_session as ( "
                        + "    insert into ew_session ( expiry_date, token, user_id ) " 
                        + "    values ( current_timestamp + interval '30 minutes', ?, ? ) " 
                        + "    returning * " 
                        + ") " 
                        + "select * " 
                        + "from ew_new_session";
                   List<EWSession> session = dataLayer.query(sql, EWSession.class,  user.getId(), uuid.toString(), user.getId());
                   resp.setStatus( "OK" );
                   resp.setToken( session.get(0 ).getToken() );
                }
                else {
                    resp.setStatus( "FAILED" );
                }
                ctx.json( resp );
            }
        });
    }
    
    public Handler doLogout() {
        return( new Handler() {
            @Override
            public void handle(Context ctx) throws Exception {
                LogoutReq req = ctx.bodyAsClass( LogoutReq.class );
                int status = -1;
                if ( ( req.getUserId() != null ) && ( req.getUserId() > 0 ) ) {
                    String sql = "update ew_session "
                               + "set expiry_date = current_timestamp "
                               + "where ( user_id = ? ) ";
                    status = dataLayer.update(sql, req.getUserId() );
                }
                else {
                    String sql = "update ew_session "
                               + "set expiry_date = current_timestamp "
                               + "where ( token = ? ) ";
                    status = dataLayer.update(sql, req.getToken() );
                }
                LogoutResp resp = new LogoutResp();
                if ( status <= 0 )
                    resp.setStatus( "FAILED" );
                else 
                    resp.setStatus( "OK" );
                ctx.json( resp );
            }
        });
    }

    public Handler showTransactions() {
        return( new Handler() {
            @Override
            public void handle(Context ctx) throws Exception {
                TransactionReq req = ctx.bodyAsClass(TransactionReq.class );
                List<EWTransaction> transList;
                if ( ( req.getFromDate() == null ) && ( req.getToDate() == null ) ) {
                    String sql = "with t as ( "
                               + "    select * "
                               + "    from ew_transaction "
                               + "    where ( wallet_id = ( select id from ew_wallet where ( user_id = ? ) ) ) "
                               + "    order by tx_date desc limit 10 "
                               + " ) "
                               + "select * "
                               + "from t "
                               + "order by tx_date asc ";
                    transList = dataLayer.query(sql, EWTransaction.class, 1L );
                }   
                else {
                    String sql = "select * "
                               + "from ew_transaction "
                               + "where ( tx_date between ? and ? ) "
                               + "  and ( wallet_id = ( select id from ew_wallet where ( user_id = ? ) ) ) ";
                    transList = dataLayer.query(sql, EWTransaction.class, req.getFromDate(), req.getToDate(), 1L );
                }
                ctx.json( transList );
            }
        });
    }
    
    public Handler showBalance() {
        return( new Handler() {
            @Override
            public void handle(Context ctx) throws Exception {
                throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
            }
        });
    }

    public Handler doCredit() {
        return( new Handler() {
            @Override
            public void handle(Context ctx) throws Exception {
                throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
            }
        });
    }
    
    public Handler doDebit() {
        return( new Handler() {
            @Override
            public void handle(Context ctx) throws Exception {
                throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
            }
        });
    }

}
