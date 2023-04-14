/**
 * @program: jfxQuery
 * @author: Leonidio Koester Junior (leo_k)
 * @date: 2023-04-07
 * @description: jfxQuery
 * @version: v1
 */

package jfxQuery;


// imports
import javafx.event.Event;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.Window;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;


// class
final public class jfxQuery {

    private final List<Object> currentMatchingSelection;

    private static Parent defaultParent = null;


    /**
     * Main class constructor
     * */
    public jfxQuery( String selectorString, Object parentElement ){
        currentMatchingSelection = new ArrayList<>();

        if ( !( parentElement instanceof Parent ) )
            parentElement = getParentReference( parentElement );

        List<jfxQuerySelectors> selectors = jfxQuerySelectors.parse( selectorString );
        findMatch( (Parent) parentElement, selectors, 0, true );

    }


    /**
     * Overloaded version supporting direct calls for the current stage
     * */
    public jfxQuery( String selectorString ){ this( selectorString, null ); }


    /**
     * Overloaded version supporting a single object (direct access)
     * */
    public jfxQuery( Object object ){ currentMatchingSelection = List.of(object); }


    /**
     * Overloaded version supporting arrays of generics
     * */
    public <T> jfxQuery( T[] array ){ currentMatchingSelection = List.of(array); }


    /**
     * Returns the default parent to be used as a reference for string selectors
     * */
    private Parent getParentReference( Object parentObject ){

        // Identifies the kind of object passed as a parent
        if ( parentObject != null ){
            if ( parentObject instanceof Window ref )
                parentObject = ref.getScene().getRoot();

            else if ( parentObject instanceof Scene ref )
                parentObject = ref.getRoot();

            else if ( !( parentObject instanceof Parent ) )
                parentObject = null;

            if ( defaultParent == null ) defaultParent = (Parent) parentObject;
        }

        if ( defaultParent == null ){

            List<Window> windows = Stage.getWindows();
            if ( windows.size() == 0 )
                throw new RuntimeException("No Stage shown during a jfxQuery selector by String");

            // defaults to the main stage
            Window window = windows.get(0);

            // tries to identify the first visible one
            List<Window> visibleWindows = windows.stream().filter(Window::isShowing).toList();
            if (visibleWindows.size() > 0) window = visibleWindows.get(0);

            parentObject = defaultParent = window.getScene().getRoot();

        }

        else if ( parentObject == null )
            parentObject = defaultParent;

        return (Parent) parentObject;
    }



    //
    //
    protected List<?> getCurrentMatchingSelection(){
        return currentMatchingSelection;
    }



    private void findMatch( Node node, List<jfxQuerySelectors> selectors, int selectorIndex, boolean keepTrying ) {

        jfxQuerySelectors selector = selectors.get(selectorIndex);
        Parent parent = null;

        if ( selector.is( node ) ){
            if ( !selector.isContainer() )
                currentMatchingSelection.add( node );

            else if ( node instanceof Parent ref ){
                parent = ref;
                selectorIndex++;
            }
        }
        else if ( node instanceof Parent ref ){
            parent = ref;
            if ( selectorIndex > 0 && selector.hasDescendants() )
                selectorIndex++;
            //keepTrying = false;
        }

        if ( parent != null && selectorIndex < selectors.size() ){
            int finalSelectorIndex = selectorIndex;
            boolean finalKeepTrying = keepTrying;
            parent.getChildrenUnmodifiable().forEach( child ->
                findMatch( child, selectors, finalSelectorIndex, finalKeepTrying )
            );
        }
    }


    /**
     * EACH
     * Traverses the last match by applying a BiFunction where:
     * [Object] represents the matched object;
     * [Integer] represents the index of that object inside the last match
     * [Boolean] - is false, will stop the iteration
     * */
    public jfxQuery each( BiFunction<Object,Integer,Boolean> biFunction ){
        return jfxQueryTraversing.each( biFunction, this );
    }


    /**
     * EACH
     * Traverses the last match by applying a BiConsumer where:
     * [Object] represents the matched object
     * */
    public jfxQuery each( Consumer<Object> consumer ){
        return each( ( object, index ) -> {
            consumer.accept( object );
            return true;
        });
    }


    /**
     * EACH
     * Traverses the last match by applying a BiConsumer where:
     * [Object] represents the matched object;
     * [Integer] represents the index of that object inside the last match
     * */
    public jfxQuery each( BiConsumer<Object,Integer> biConsumer ){
        return each(( object, index ) -> {
            biConsumer.accept( object, index );
            return true;
        });
    }


    /**
     * EACH
     * Traverses the last match by applying a Function where:
     * [Object] represents the matched object;
     * [Boolean] - is false, will stop the iteration
     * */
    public jfxQuery each( Function<Object,Boolean> function ){
        return each(( object, index ) -> {
            return function.apply( object );
        });
    }



    //
    // Manipulators
    /**
     * ATTR - SETTER
     * */
    public jfxQuery attr( String attributeName, Object value ){
        return each( (Consumer<Object>) obj -> jfxQueryCollector.define( obj, attributeName, value ) );
    }


    /**
     * TEXT()
     * */
    // getter
    public String text(){
        if ( currentMatchingSelection.size() > 0 )
            return jfxQueryCollector.collect( currentMatchingSelection.get(0), "text" );
        return null;
    }
    // setter
    public jfxQuery text( String value ){
        return attr( "text", value );
    }


    /**
     * CSS()
     * */
    public jfxQuery css( String cssAttribute, Object value ){
        each( obj -> {
            jfxQueryStylist.updateStyle( ((Node) obj), Map.of(cssAttribute, value) );
        });
        return this;
    }
    public jfxQuery css( Map<String,Object> cssAttributes ){
        each( obj -> {
            jfxQueryStylist.updateStyle( ((Node) obj), cssAttributes );
        });
        return this;
    }



    /**
     * GET()
     * */
    public Node get( int index ){
        return (Node) jfxQueryCollector.get( index, this );
    }


    //
    // Event Handlers
    /**
     *
     * */
    public jfxQuery on( String onEvent, BiConsumer<Node,Event> consumer ){
        return jfxQueryEventHandlers.on( onEvent, consumer, this );
    }



}
