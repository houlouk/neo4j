package org.neo4j.graphalgo.util;

import org.neo4j.graphalgo.util.PriorityMap.Converter;
import org.neo4j.graphalgo.util.PriorityMap.Entry;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.traversal.ExpansionSource;
import org.neo4j.graphdb.traversal.SourceSelector;
import org.neo4j.graphdb.traversal.SourceSelectorFactory;

public abstract class BestFirstSelectorFactory<P extends Comparable<P>, D>
        implements SourceSelectorFactory
{
    public SourceSelector create( ExpansionSource startSource )
    {
        return new BestFirstSelector( startSource, getStartData() );
    }
    
    protected abstract P getStartData();

    public final class BestFirstSelector implements SourceSelector
    {
        private PriorityMap<ExpansionSource, Node, P> queue =
                PriorityMap.withNaturalOrder( CONVERTER );
        private ExpansionSource current;
        private P currentAggregatedValue;

        public BestFirstSelector( ExpansionSource source, P startData )
        {
            this.current = source;
            this.currentAggregatedValue = startData;
        }

        public ExpansionSource nextPosition()
        {
            // Exhaust current if not already exhausted
            while ( true )
            {
                ExpansionSource next = current.next();
                if ( next != null )
                {
                    P newPriority = addPriority( next, currentAggregatedValue,
                            calculateValue( next ) );
                    queue.put( next, newPriority );
                }
                else
                {
                    break;
                }
            }
            
            // Pop the top from priorityMap
            Entry<ExpansionSource, P> entry = queue.pop();
            if ( entry != null )
            {
                current = entry.getEntity();
                currentAggregatedValue = entry.getPriority();
                return current;
            }
            return null;
        }

    }

    protected abstract P addPriority( ExpansionSource source,
            P currentAggregatedValue, D value );
    
    protected abstract D calculateValue( ExpansionSource next );
    
    public static final Converter<Node, ExpansionSource> CONVERTER =
            new Converter<Node, ExpansionSource>()
    {
        public Node convert( ExpansionSource source )
        {
            return source.node();
        }
    };
}
