package org.basalt.ewallet;

import org.basalt.ewallet.messageclasses.CreateAccountResp;
import org.basalt.ewallet.messageclasses.CreateAccountReq;
import com.google.gson.Gson;
import io.javalin.Javalin;
import io.javalin.http.Context;
import io.javalin.http.Handler;
import java.util.List;
import java.util.UUID;
import org.basalt.ewallet.dataclasses.EWSession;
import org.basalt.ewallet.dataclasses.EWUser;
import org.basalt.ewallet.messageclasses.LoginReq;
import org.basalt.ewallet.messageclasses.LoginResp;

public class EWallet {
    
    Javalin javalin;
    DataLayer dataLayer;
    Gson gson;
    
    public EWallet( DataLayer dataLayer ) {
        this.dataLayer = dataLayer;
        gson = new Gson();
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
        javalin.start( 8000 );
    }
    
    public Handler createAccount() {
        return( new Handler() {
            
            @Override
            public void handle(Context ctx) throws Exception {
                CreateAccountReq req = ctx.bodyAsClass(CreateAccountReq.class );
                String sql = "insert into ew_user ( username, passhash ) "
                           + "values ( ?, ? )";
                Long userId = dataLayer.insertWithId(sql, req.getUsername(), req.getPasshash() );
                
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
                List<EWUser> userList = dataLayer.query(sql, EWUser.class, req.getUsername(), req.getPassHash() );
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
                throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
            }
        });
    }

    public Handler showTransactions() {
        return( new Handler() {
            @Override
            public void handle(Context ctx) throws Exception {
                throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
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
