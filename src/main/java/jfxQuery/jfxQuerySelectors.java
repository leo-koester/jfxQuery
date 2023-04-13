/**
 * @program: jfxQuerySelectors
 * @author: Leonidio Koester Junior (leo_k)
 * @date: 2023-04-10
 * @description: jfxQuerySelectors
 * @version: v1
 */

package jfxQuery;


//import
import javafx.scene.Node;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


// class
public class jfxQuerySelectors {


    public static final String PATTERN_MATCHER = "(?im)"+
        "(?<selector>(?:[\\w\\.]+|\\*)|)"+
        "(?<attr>\\[(?<attrName>\\w+)(?:(?<attrMatcher>[\\^\\*\\$\\!]*=|)(?<attrValue>[^\\]]+?)|)\\]|)"+
        "(?::(?<fn>not|has)\\((?<fnSelectors>.*)\\)|)"+
        "(?<cls>\\.\\w+|)"+
        "(?:\\s*(?<combinator>[>~+ ]|\\s)\\s*|)"
    ;


    //
    public enum Combinator {
        //
        CHILDREN('>'),
        DESCENDANTS(' '),
        SIBLING('~'),
        ADJACENT('~');

        //
        final char identifier;

        //
        Combinator( char type ){
            this.identifier = type;
        }

        //
        public static Combinator fromChar( String descendantType ) {
            if ( descendantType != null ){
                char type = ( descendantType.trim() + " " ).charAt( 0 );

                for ( Combinator comb : Combinator.values() )
                    if ( type == comb.identifier ) return comb;
            }
            return null;
        }
    }


    // members
    final private String selector;
    final private Combinator familyType;
    final private jfxQueryAttribute attribute;


    // constructors
    public jfxQuerySelectors( Matcher matcher ){

        String selector = matcher.group("selector");
        if ( selector == null ) selector = "*";
        this.selector = selector.trim();

        String attr = matcher.group("attrName");
        this.attribute = attr == null ? null
            : new jfxQueryAttribute(
                attr,
                matcher.group( "attrMatcher" ),
                (matcher.group( "attrValue" )+" ").trim().replaceAll( "^['\"](.*?)['\"]$", "$1" )
            )
        ;

        this.familyType = Combinator.fromChar(matcher.group("combinator"));

    }


    public boolean isContainer(){
        return familyType != null;
    }


    public boolean hasDescendants(){
        return familyType == Combinator.DESCENDANTS;
    }

    public boolean hasChildren(){
        return familyType == Combinator.CHILDREN;
    }


    //
    //
    public boolean is( Node node ){
        String clsName = node.getClass().getSimpleName();
        if ( selector.charAt( 0 ) == '.' ) clsName = "."+ clsName.toLowerCase();

        boolean result = clsName.equals( selector );
        if ( result && attribute != null ){
            result = jfxQueryCollector.match( node,
                attribute.name,
                attribute.matcher,
                attribute.value
            );
        }

        return result;
    }


    //
    //
    private record jfxQueryAttribute( String name, String matcher, String value ){

    }


    @Override
    public String toString(){
        return selector+" "+attribute+" "+ familyType;
    }




    //
    public static List<jfxQuerySelectors> parse( String selectors ){
        List<jfxQuerySelectors> result = new ArrayList<>();

        Matcher matcher = Pattern.compile( PATTERN_MATCHER ).matcher( selectors );
        while ( matcher.find() ){
            result.add( new jfxQuerySelectors( matcher ) );
            if ( matcher.hitEnd() ) break;
        }

        return result;
    }

}
