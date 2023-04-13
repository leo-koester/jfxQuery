package jfxQuery;


// imports
import javafx.scene.Node;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;


// interface
public interface jfxQueryCollector {


    //
    // collects an attribute from the Node
    static String collect( Object obj, String attributeName ){
        return getAttributeValue( obj, attributeName );
    }


    //
    // checks if attributeName matches node's
    static boolean match( Object obj, String attributeName, String matcher, String attrValue ){

        String value = getAttributeValue( obj, attributeName );
        if ( value == null ) return false;

        if ( matcher == null )
            return !value.equals( "false" );

        else return switch ( matcher ){
            case "="  -> value.equals( attrValue );
            case "*=" -> value.contains( attrValue );
            case "^=" -> value.startsWith( attrValue );
            case "$=" -> value.endsWith( attrValue );
            case "!=" -> !value.equals( attrValue );
            default   -> false;
        };
    }


    //
    // retrieves a node's method
    private static Method getMethodFromObject( Object obj, String... methodNames ) {
        Class<?> cls = obj.getClass();

        do {
            Method[] methods = cls.getDeclaredMethods();
            for ( Method method : methods ){
                String methodName = method.getName();
                for ( String methodNameBeingTested : methodNames )
                    if ( methodName.equalsIgnoreCase( methodNameBeingTested ) )
                        return method;
            }
            cls = cls.getSuperclass();
        }
        while ( cls != null );

        return null;
    }



    //
    //
    private static String getAttributeValue( Object obj, String attributeName ){

        Method method = getMethodFromObject(
            obj,
            "get"+attributeName,
            "is"+attributeName,
            "has"+attributeName
        );
        try {
            assert method != null;
            return method.invoke( obj ).toString();
        }
        catch ( InvocationTargetException | IllegalAccessException e ){
            throw new RuntimeException( e );
        }

    }


    //
    // define -
    static void define( Object obj, String attributeName, Object... value ){
        Method method = getMethodFromObject( obj, "set"+attributeName );

        try {
            assert method != null;
            if ( method.toString().contains( "public" ) ){
                System.out.println( method );
                method.invoke( obj, value );
            }

            else if ( obj instanceof Node node ){
                switch ( attributeName ){
                    case "disabled" -> node.setDisable( (Boolean) value[0] );
                    default -> System.out.println( "\u001B[33mCannot define '"+method+"' for object "+ obj+"\u001B[0m" );
                }
            }
        }
        catch ( InvocationTargetException | IllegalAccessException e ){
            throw new RuntimeException( e );
        }
    }


    //
    // get - returns an object from the list
    static Object get( int index, jfxQuery jfxQObj ) {

        List<?> list = jfxQObj.getCurrentMatchingSelection();
        return ( list.size() > 0 ) ? list.get( index ) : null;

    }
}
