package org.basalt.ewallet;

import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.sql.DataSource;

public class DataLayer {

    DataSource ds;
    
    public DataLayer( DataSource ds ) {
        this.ds = ds;
    }
    
    protected Map<Class<?>,Map<String,Method>> methodCache = new HashMap<>();
    
    protected String[] getColumnNames( ResultSetMetaData metaData ) throws Exception {
        List<String> columnNames = new ArrayList<>();
        for ( int i = 0; i < metaData.getColumnCount(); i++ ) {
            columnNames.add( metaData.getColumnName(i));
        }
        return( columnNames.toArray( new String[columnNames.size()]));
    }
    
    protected String dbToCamel( String dbName ) {
        boolean upperCase = true;
        StringBuffer camelName = new StringBuffer( "" );
        for ( int i = 0; i < dbName.length(); i++ ) {
            if ( upperCase == true ) {
                camelName.append( Character.toUpperCase( dbName.charAt(i) ) );
                upperCase = false;
            }
            else if ( dbName.charAt( i ) == '_' ) {
                upperCase = true;
            }
            else {
                camelName.append( Character.toLowerCase( dbName.charAt( i ) ) );
            }
        }
        return( camelName.toString() );
    }
    
    protected void mapMethods( String[] columnNames, Map<String,Method> methods, Class<?> classType ) {
        Method[] classMethods = classType.getMethods();
        Map<String,Method> setterMethods = new HashMap<String,Method>();
        for ( int i = 0; i < classMethods.length; i++ ) {
            Method method = classMethods[i];
            if ( method.getName().startsWith("set" ) &&
                 method.getParameterCount() == 1 ) {
                setterMethods.put( method.getName(), method );
            }
        }
        
        for ( int i = 0; i < columnNames.length; i++ ) {
            String methodName = "set" + dbToCamel( columnNames[i] );
            Method method = setterMethods.get( methodName );
            if ( method != null ) {
                methods.put( columnNames[i], method );
            }
        }
    }
    
    public <T> List<T> rsToObject( ResultSet rs, Class<T> classType ) {
        List<T> results = new ArrayList<>();
        try {
            
            ResultSetMetaData metaData = rs.getMetaData();
            String[] columnNames = getColumnNames( metaData );
            
            Map<String,Method> methods = methodCache.getOrDefault(classType, new HashMap<String,Method>() );
            if ( methods.isEmpty() ) {
                mapMethods( columnNames, methods, classType );
                methodCache.put( classType, methods );
            }
            
            while ( rs.next() ) {
                Object t = classType.getConstructor().newInstance();
                for ( int i = 0; i < columnNames.length; i++ ) {
                    String colName = columnNames[i];
                    Object o = rs.getObject( colName );
                    Method m = methods.get( colName );
                    if ( m != null ) {
                        m.invoke(t, o );
                    }
                }
            }
        }
        catch ( Exception e ) {
            e.printStackTrace();
        }
        return( results );
    }

    public <T> List<T> query( String sql, Class<T> classType, Object ... params ) throws Exception {
        List<T> results = new ArrayList<>();
        try {
            Connection conn = ds.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            for ( int i = 0; i < params.length; i++ ) {
                Object o = params[i];
                ps.setObject(i + 1, o);
            }
            ResultSet rs = ps.executeQuery();
            results = rsToObject(rs, classType);
            rs.close();
            ps.close();
            conn.close();
        }
        catch ( Exception e ) {
            e.printStackTrace();
        }
        return( results );
    }
    
    public Long insertWithId( String sql, Object ... params ) {
        try {
            Connection conn = ds.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS);
            for ( int i = 0; i < params.length; i++ ) {
                Object o = params[i];
                ps.setObject(i + 1, o);
            }
            ps.execute();
            ResultSet rs = ps.getGeneratedKeys();
            rs.next();
            return( rs.getLong( 1 ) );
        }
        catch ( Exception e ) {
            e.printStackTrace();
        }
        return( 0L );
    }
    
    
    
}
