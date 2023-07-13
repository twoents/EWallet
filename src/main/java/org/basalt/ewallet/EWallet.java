package org.basalt.ewallet;

import org.basalt.ewallet.messageclasses.CreateAccountResp;
import org.basalt.ewallet.messageclasses.CreateAccountReq;
import com.google.gson.Gson;
import io.javalin.Javalin;
import io.javalin.http.Context;
import io.javalin.http.Handler;

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
                
                
                throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
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
