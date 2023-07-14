package org.basalt.ewallet;

import org.basalt.ewallet.messageclasses.CreateAccountResp;
import org.basalt.ewallet.messageclasses.CreateAccountReq;
import io.javalin.Javalin;
import io.javalin.http.Context;
import io.javalin.http.Handler;
import io.javalin.http.servlet.JavalinServletContext;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import org.basalt.ewallet.dataclasses.EWLoginQuery;
import org.basalt.ewallet.dataclasses.EWSession;
import org.basalt.ewallet.dataclasses.EWTransaction;
import org.basalt.ewallet.dataclasses.EWUser;
import org.basalt.ewallet.dataclasses.EWWallet;
import org.basalt.ewallet.messageclasses.BalanceReq;
import org.basalt.ewallet.messageclasses.BalanceResp;
import org.basalt.ewallet.messageclasses.CreditResp;
import org.basalt.ewallet.messageclasses.DebitReq;
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
                String authHeader = ctx.header( "Authorization" );
                String token = authHeader.substring(6 );
                
                String sql = "with s as ( "
                           + "  update ew_session "
                           + "  set expiry_date = current_timestamp "
                           + "  where ( token = ? ) "
                           + "    and ( expiry_date < current_timestamp ) "
                           + "  returning * "
                           + ") "
                           + "select * from t ";
                List<EWSession> sessionList = dataLayer.query( sql, EWSession.class, token );
                if ( !sessionList.isEmpty() ) {
                    ctx.attribute("userId", sessionList );
                }
                else {
                    ctx.status( 401 );
                    ( ( JavalinServletContext )ctx ).getTasks().clear();
                }
            }
        });
    }
    
    public Handler createAccount() {
        return( new Handler() {
            
            @Override
            public void handle(Context ctx) throws Exception {
                CreateAccountReq req = ctx.bodyAsClass(CreateAccountReq.class );
                String sql = "with t as ( "
                           + "  insert into ew_user ( username, passhash ) "
                           + "  values ( ?, ? ) "
                           + "  returning * "
                           + "), "
                           + "w as ( "
                           + "    insert into ew_wallet "
                           + "   ( wal_name, balance, user_id ) "
                           + "    values ( 'primary', 0.00, ( select id from t ) ) "
                           + "    returning * "
                           + ") "
                           + "select * "
                           + "from t ";                
                List<EWUser> userList = dataLayer.query(sql, EWUser.class, req.getUsername(), hashPassword( req.getPassword(),req.getUsername() ) );
                ctx.json( new CreateAccountResp( userList.get( 0 ).getId() ) );
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
                        + "select *, "
                        + "       ( select id from ew_wallet where ( user_id = ? ) ) as wallet_id " 
                        + "from ew_new_session";
                   List<EWLoginQuery> session = dataLayer.query(sql, EWLoginQuery.class,  user.getId(), uuid.toString(), user.getId(), user.getId() );
                   resp.setStatus( "OK" );
                   resp.setToken( session.get(0 ).getToken() );
                   resp.setWalletId(session.get(0 ).getWalletId());
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
                               + "    where ( wallet_id = ? ) "
                               + "    order by tx_date desc limit 10 "
                               + " ) "
                               + "select * "
                               + "from t "
                               + "order by tx_date asc ";
                    transList = dataLayer.query(sql, EWTransaction.class, req.getWalletId() );
                }   
                else {
                    String sql = "select * "
                               + "from ew_transaction "
                               + "where ( tx_date between ? and ? ) "
                               + "  and ( wallet_id = ? ) ";
                    transList = dataLayer.query(sql, EWTransaction.class, req.getFromDate(), req.getToDate(), req.getWalletId() );
                }
                ctx.json( transList );
            }
        });
    }
    
    public Handler showBalance() {
        return( new Handler() {
            @Override
            public void handle(Context ctx) throws Exception {
                
                Long userId = ctx.attribute("user_id" );
                
                BalanceReq req = ctx.bodyAsClass(BalanceReq.class );
                String sql = "select * "
                           + "from ew_wallet "
                           + "where ( user_id = ? ) ";
                List<EWWallet> walletList = dataLayer.query(sql, EWWallet.class, userId );
                BalanceResp resp = new BalanceResp();
                resp.setBalance( walletList.get( 0 ).getBalance() );
                resp.setWalName( walletList.get( 0 ).getWalName() );
                ctx.json(resp);
            }
        });
    }

    public Handler doCredit() {
        return( new Handler() {
            @Override
            public void handle(Context ctx) throws Exception {
                Long userId = ctx.attribute("user_id" );
                DebitReq req = ctx.bodyAsClass( DebitReq.class );
                String sql = "with t as ( " 
                           + "    insert into ew_transaction " 
                           + "    ( description, tx_date, amount, wallet_id ) " 
                           + "    values ( ?, current_timestamp, ?, ? ) " 
                           + "), " 
                           + "w as ( " 
                           + "    update ew_wallet " 
                           + "    set balance = balance + 10 " 
                           + "    where ( id = ? ) " 
                           + "    returning * " 
                           + ") " 
                           + "select * " 
                           + "from w ";
                List<EWWallet> walletList = dataLayer.query(sql, EWWallet.class, req.getDescription(), req.getAmount().multiply( BigDecimal.valueOf( -1L ) ), req.getWalletId(), req.getAmount() );
                CreditResp resp = new CreditResp();
                resp.setWalletId( walletList.get( 0 ).getId() );
                resp.setBalance(walletList.get( 0 ).getBalance() );
                ctx.json( resp );
            }
        });
    }
    
    public Handler doDebit() {
        return( new Handler() {
            @Override
            public void handle(Context ctx) throws Exception {
                Long userId = ctx.attribute("user_id" );
                DebitReq req = ctx.bodyAsClass( DebitReq.class );
                String sql = "with t as ( " 
                           + "    insert into ew_transaction " 
                           + "    ( description, tx_date, amount, wallet_id ) " 
                           + "    values ( ?, current_timestamp, ?, ? ) " 
                           + "), " 
                           + "w as ( " 
                           + "    update ew_wallet " 
                           + "    set balance = balance + 10 " 
                           + "    where ( id = ? ) " 
                           + "    returning * " 
                           + ") " 
                           + "select * " 
                           + "from w ";
                List<EWWallet> walletList = dataLayer.query(sql, EWWallet.class, req.getDescription(), req.getAmount(), req.getWalletId(), req.getAmount() );
                CreditResp resp = new CreditResp();
                resp.setWalletId( walletList.get( 0 ).getId() );
                resp.setBalance(walletList.get( 0 ).getBalance() );
                ctx.json( resp );
            }
        });
    }

}
