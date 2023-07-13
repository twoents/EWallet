package org.basalt.ewallet;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;
import org.apache.commons.dbcp2.BasicDataSource;

public class WebMain {

    EWallet eWallet;
    DataLayer dataLayer;

    Properties loadConfig( String[] args ) throws Exception {
        Properties props = new Properties();
        InputStream propInputStream = null;
        if ( args.length != 0 ) {
            propInputStream = new FileInputStream( args[0] );
        }
        else {
            propInputStream = this.getClass().getClassLoader().getResourceAsStream( "config.properties" );        
        }
        props.load(propInputStream);
        return( props );
    }
    
    protected void init( Properties config ) throws Exception {
        
        BasicDataSource ds = new BasicDataSource();
        ds.setUrl(config.getProperty( "dbUrl" ) );
        ds.setUsername(config.getProperty("dbUsername" ) );
        ds.setPassword(config.getProperty("dbPassword" ) );
        ds.setMinIdle(5);
        ds.setMaxIdle(10);
        ds.setMaxOpenPreparedStatements(100);

        dataLayer = new DataLayer(ds);
        eWallet = new EWallet( dataLayer );
        eWallet.init();
    }
    
    public static void main(String[] args) {

        try {
            WebMain webMain = new WebMain();
            Properties config = webMain.loadConfig(args);
            webMain.init( config );
        }
        catch ( Exception e ) {
            e.printStackTrace();
        }

    }
}
