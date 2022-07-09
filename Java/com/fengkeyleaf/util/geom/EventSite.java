package com.fengkeyleaf.util.geom;

/*
 * EventSite.java
 *
 * JDK: 15
 *
 * Version:
 *     $1.0$
 *
 * Revisions:
 *     $1.0 basic operations on 12/29/2021$
 */

import com.fengkeyleaf.lang.MyMath;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Data structure of Event edge for Voronoi Diagrams
 *
 * @author Xiaoyu Tongyang, or call me sora for short
 * @see <a href="https://fengkeyleaf.com">person website</a>
 * @since  1.0
 */

class EventSite extends Event {
    private static int IDStatic = 0;
    public final Type type;

    enum Type {
        // events
        SITE, CIRCLE,
        // status
        INTERNAL, LEAF,
        // special dangling half-edge,
        // namely, vertical half-edge at the very beginning,
        // the direction that the half-edge is extending is
        // the opposite of that of its breakpoint.
        SPECIAL
    }

    // tree node of this event.
    VorRBTNode node;

    // site stored in VoronoiFace,
    // which event and status both have.
    VoronoiFace siteFace;

    //-------------------------------------------------------
    // events queue, Q
    //-------------------------------------------------------

    // only circle event has.
    // <pj, pi, pk>
    final List<VorRBTNode> triple = new ArrayList<>( 3 );
    // <pj, pi>, <pj, pk>.
    Internal internals;
    // circle that this Voronoi vertex has.
    Circle circle;
    // corresponding leaf node in the status tree of this circle event.
    VorRBTNode leaf;
    // breakpoint that cannot be merged when we have several sites that coincide,
    // and one of them is at the lowest point of the circle.
    VoronoiFace specTwin;

    /**
     * inner class to store the two internal nodes of a circle event.
     * triple: (pj, pi, pk)
     * internals: <pj, pi> <pi, pk>
     */

    private static class Internal {
        // <pj, pi>, internal status node
        final VorRBTNode leftBreakpoint;
        // <pi, pk>, internal status node
        final VorRBTNode rightBreakpoint;

        /**
         * Constructs to create an instance of Internal
         */

        Internal( VorRBTNode left,
                  VorRBTNode right ) {
            assert left.key.type == Type.INTERNAL : left + " " + right;
            assert right.key.type == Type.INTERNAL;

            leftBreakpoint = left;
            rightBreakpoint = right;
        }

        /**
         * Update the tuples representing the breakpoints at the internal noes.
         * <pj, pi> <pi, pk> => <pj, pk>
         */

        VorRBTNode update( VorRBTNode node,
                                                 StatusRBTreeVoronoi statusTree ) {
            addSpecial( statusTree );

            // deleted node is the left breakpoint.
            if ( leftBreakpoint == node ) {
                rightBreakpoint.key.leftArc = leftBreakpoint.key.leftArc;
                rightBreakpoint.getBisector();

                return rightBreakpoint;
            }

            // deleted node is the right breakpoint.
            assert rightBreakpoint == node : rightBreakpoint.key + " " + node.key;
            leftBreakpoint.key.rightArc = rightBreakpoint.key.rightArc;
            leftBreakpoint.getBisector();

            return leftBreakpoint;
        }

        /**
         * add special dangling half-edge.
         */

        void addSpecial( StatusRBTreeVoronoi statusTree ) {
            int count = 0;
            if ( isDegenerate( leftBreakpoint.key, statusTree.initY ) ) {
                statusTree.addSpecialDangling( new EventSite( leftBreakpoint.key ) );
                count++;
            }

            if ( isDegenerate( rightBreakpoint.key, statusTree.initY ) ) {
                statusTree.addSpecialDangling( new EventSite( rightBreakpoint.key ) );
                assert ++count < 2;
            }
        }

        /**
         * is the degenerate case?
         * i.e. the sweep line is at the very beginning of the algorithm.
         */

        // sites( vectors ) are initialized directly from input data,
        // and cannot be changed, as well as the initY,
        // so cannot have precision issue.
        boolean isDegenerate( EventSite e, double initY ) {
            return e.leftArc.site.y == e.rightArc.site.y
                    && e.rightArc.site.y == initY;
        }

        HalfEdge[] getEdges() {
            return new HalfEdge[] { leftBreakpoint.key.edge, rightBreakpoint.val.edge };
        }

        @Override
        public String toString() {
            return "LBre: " + leftBreakpoint + " | RBre:" + rightBreakpoint;
        }
    }

    //-------------------------------------------------------
    // status structure, T
    //-------------------------------------------------------

    // breakpoint, <pj, pi>, pj is the left arc, pi the right arc,
    // which interval node has
    VoronoiFace leftArc;
    VoronoiFace rightArc;

    // halfEdge facing to the left arc( voronoi face ),
    // which only interval node has.
    HalfEdge edge;
    // bisector( line ) splitting pj and pi.
    Line bisector;
    // direction this breakpoint is moving along
    Vector direction;

    // corresponding circle events in the event queue,
    // which only leaf node has.
    VorRBTNode circleEvent;
    VorRBTNode leftCircleEvent;
    VorRBTNode rightCircleEvent;

    /**
     * Constructs to create an instance of EventEdge, status ( internal node )
     */

    EventSite( double x, double y,
                      VoronoiFace left, VoronoiFace right,
                      Line bisector ) {
        super( x, y, IDStatic++ );
        this.leftArc = left;
        this.rightArc = right;
        this.bisector = bisector;
        type = Type.INTERNAL;
    }

    EventSite( Vector point, VoronoiFace left,
                      VoronoiFace right, Line bisector ) {
        this( point.x, point.y, left, right, bisector );
    }

    /**
     * Constructs to create an instance of EventEdge, site event or leaf node ( status )
     */

    EventSite( VoronoiFace site, Type type ) {
        super( site.site.x, site.site.y, IDStatic++ );
        siteFace = site;
        this.type = type;
    }

    /**
     * Constructs to create an instance of EventEdge, circle event
     */

    EventSite( Circle circle, Vector lowest ) {
        super( lowest.x, lowest.y, IDStatic++ );
        this.circle = circle;
        this.type = Type.CIRCLE;
    }

    /**
     * Constructs to create an instance of EventEdge, special dangling half-edge
     */

    // only copy data field for the speical dangling.
    @SuppressWarnings( "all" )
    EventSite( EventSite event ) {
        super( event.x, event.y, IDStatic++ );
        edge = event.edge;
        leftArc = event.leftArc;
        rightArc = event.rightArc;

        bisector = event.bisector;
        type = Type.SPECIAL;
        assert check();
    }

    private boolean check() {
        assert edge != null;
        assert leftArc != null;
        assert rightArc != null;

        return true;
    }

    /**
     * get site event ( intersection point or breakpoint ) between two arcs
     */

    static
    EventSite getSiteEvent( Vector pi, Vector pj,
                            StatusRBTreeVoronoi statusTree,
                            VoronoiFace left, VoronoiFace right ) {
        if ( statusTree.initY == pi.y )
            return new EventSite( pi, left, right, Lines.getBisector( pi, pj ) );

        // get the parabola above pi, which is pj.
        Parabola parabola = new Parabola( pj, statusTree.sweepY );
        return new EventSite( pi.x, parabola.updateY( pi.x ), left, right, Lines.getBisector( pi, pj ) );
    }

    /**
     * add two internal nodes of this circle event.
     */

    void addInternals( VorRBTNode left, VorRBTNode right ) {
        internals = new Internal( left, right );
    }

    /**
     * update two internal nodes of this circle event.
     */

    VorRBTNode update( VorRBTNode node, StatusRBTreeVoronoi statusTree ) {
        return internals.update( node, statusTree );
    }

    HalfEdge[] getEdges() {
        return internals.getEdges();
    }

    /**
     * 3.6 update nodes in the triple in a circle event, if exists.
     */

    void updateTriple( StatusRBTreeVoronoi statusTree ) {
        // TODO: 2/12/2022 update left may be redundant
        // update left circle event involving this leaf node.
        if ( leftCircleEvent != null )
            leftCircleEvent.key.updateTriple( statusTree, false );

        // update right circle event involving this leaf node.
        if ( rightCircleEvent != null )
            rightCircleEvent.key.updateTriple( statusTree, true );
    }

    /**
     * <pt, pj>, <pj, pk>, meaning we have the triple: (pt, pj, pk).
     * assume that we replace pj with pi's nodes.
     * => <pt, pj>, <pj, pi>, <pi, pj>, <pj, pk>
     * and the triple ( also a circle event associated with) will be removed.
     * so we need to check adjacent circle events' triples:
     * before: <pa, pt>, <pt, pj>, <pj, pk>, <pk, pb>,
     * meaning we have three triples: (pa, pt, pj), (pt, pj, pk), (pj, pk, pb).
     * after: <pa, pt>, <pt, pj>, <pj, pi>, <pi, pj>, <pj, pk>, <pj, pk>,
     * meaning we have two triples: (pa, pt, pj), (pj, pk, pb).
     *
     * And you may notice that the symbols may not change,
     * but in the actual R-B tree, the pointers have changed.
     */

    private void updateTriple( StatusRBTreeVoronoi statusTree, boolean isLeft ) {
        VorRBTNode pre = ( VorRBTNode ) statusTree.predecessor( leaf );

        // no predecessor, no update
        if ( isLeft && pre != null ) {
            assert pre.node.getPrev().getData().key.type == Type.LEAF : leaf + ", " + pre + " " + pre.node.getPrev().getData();
            triple.set( 0, ( VorRBTNode ) pre.node.getPrev().getData() );
            return;
        }

        VorRBTNode succ = ( VorRBTNode ) statusTree.successor( leaf );
        // no successor, no update
        if ( succ == null ) return;
        assert succ.node.getNext().getData().key.type == Type.LEAF;
        triple.set( 2, ( VorRBTNode ) succ.node.getNext().getData() );
    }

    /**
     * delete a Circle Event, including removing the circle event in the queue and relationship.
     * */

    void deleteCircleEvent( EventRBTreeVoronoi eventQueue ) {
        if ( circleEvent != null ) {
            assert circleEvent.key.type == Type.CIRCLE;
            // remove adjacent circle events before deleting it from the event queue.
            // since the pointer pointing to the event may change to other node after deleting.
            circleEvent.key.deleteCircleEvent();

            eventQueue.delete( circleEvent );
            circleEvent = null;
        }
    }

    /**
     * remove the circle event relationship
     * between a leaf status node with its predecessor and successor.
     *
     * before:
     *
     *                |-> ...
     * predecessor    ->  circleEvent
     *                |-> leaf node's circle event
     *
     *                |-> predecessor's circle event
     * leaf node      ->  circleEvent
     *                |-> successor's circle event
     *
     *                |-> leaf node's circle event
     * successor node ->  circleEvent
     *                |-> ...
     *
     * after:
     *
     *                |-> ...
     * predecessor    ->  circleEvent
     *                |-> null
     *
     *                |-> predecessor's circle event
     * leaf node      ->  null
     *                |-> successor's circle event
     *
     *                |-> null
     * successor node ->  circleEvent
     *                |-> ...
     * */

    void deleteCircleEvent() {
        assert type == Type.CIRCLE : this;

        triple.get( 0 ).key.rightCircleEvent = null;
        triple.get( 2 ).key.leftCircleEvent = null;
    }

    /**
     * update tree node of this event, mainly when deleting an event from a R-B tree.
     * You may wonder why we should do this? since we just delete a node, not adding one.
     * But recall we may replace the successor of the node to be deleted at its place,
     * thus the tree node of the successor will change.
     * */

    void updateNode( VorRBTNode node ) {
        if ( type == Type.CIRCLE )
            leaf.val.circleEvent = node;
    }

    /**
     * Handling a new site event, we need replace pj with pi,
     * and pj's rightCircleEvent is not null, so
     * the right pj leaf node needs to inherit its rightCircleEvent.
     * */

    // this one is left
    void updateCircleEvent( EventSite right ) {
        assert type == Type.LEAF;

        if ( rightCircleEvent != null ) {
            right.rightCircleEvent = rightCircleEvent;
            rightCircleEvent = null;
        }
    }

    /**
     * the sweep line is at the initializing position?
     * Be careful with processing Site node and Internal node,
     * they are a bit different.
     * */

    boolean isOnInitSweep( double initY ) {
        if ( type == Type.SITE )
            return MyMath.isEqual( y, initY );

        assert type == Type.INTERNAL : type;
        return MyMath.isEqual( leftArc.site.y, initY ) &&
                MyMath.isEqual( rightArc.site.y, initY );
    }

    /**
     * the node we're processing is on the sweep line?
     * */

    boolean isOnSweepLine( double sweepY ) {
        if ( type == Type.SITE ) return true;

        assert type == Type.INTERNAL;
        return MyMath.isEqual( leftArc.site.y, sweepY ) ||
                MyMath.isEqual( rightArc.site.y, sweepY );
    }

    @Override
    public void update( Vector update, boolean isUpdatingByX ) {}

    /**
     * actually updateX( double y ) with the breakpoints of left and right arcs
     */

    @Override
    public void update( double coor, boolean isUpdatingByX ) {
        Line directrix = new Line( new Vector( 1, coor ), new Vector( 2, coor ) );
        assert directrix.isHorizontal;
        Parabola left = new Parabola( leftArc.site, directrix );
        Parabola right = new Parabola( rightArc.site, directrix );

        Vector[] intersections = left.intersect( right );
        assert intersections != null : leftArc + " " + rightArc + " " + coor;

        if ( intersections.length == 1 ) {
            this.setXAndY( intersections[ 0 ] );
            return;
        }

        Arrays.sort( intersections, Vectors::sortByX );
        if ( leftArc.site.isBelow( rightArc.site ) ) {
            assert rightArc.site.isRight( intersections[ 1 ] ) : this + " " + coor + " " + Arrays.toString( intersections );
            setXAndY( intersections[ 0 ] );
            return;
        }

        assert rightArc.site.isBelow( leftArc.site ) : rightArc.site + " " + leftArc.site;
        assert leftArc.site.isRight( intersections[ 1 ] ) : this + " " + coor + " " + leftArc.site + " " + Arrays.toString( intersections );
        setXAndY( intersections[ 1 ] );
    }

    /**
     * get the direction that this breakpoing is moving along.
     * */

    void getDirection( double sweepY ) {
        EventSite endPoint = new EventSite( this );
        endPoint.update( sweepY, true );
        direction = endPoint.subtract( this );
    }

    /**
     * this internal has twin breakpoint that cannot be merged with it?
     * A typical situation is where several sites coincide and
     * one of them is at the lowest point of the circle.
     * */

    boolean isNotSpecial( EventSite e ) {
        assert type == Type.CIRCLE;

        if ( specTwin != null )
            return specTwin != e.leftArc && specTwin != e.rightArc;

        return true;
    }

    /**
     * is there a normal circle event between those two triples( breakpoints )?
     * This method handles normal cases. i.e. no sites overlapping a circle event.
     */

    // TODO: 1/1/2022 implement, put this in the status tree, and have a topmost structure to have the queue and BBST
    static
    boolean hasCircleEvent( EventSite gamma, EventSite current,
                            EventSite other, double sweepY ) {

        // degenerate case: several sites that coincide.
        if ( current.equalsXAndY( other ) &&
                // avoid merging twin brekapoint.
                gamma.isNotSpecial( other ) ) return true;

        return hasCircleEvent( current, other, sweepY );
    }

    /**
     * is there a normal or special circle event between those two triples( breakpoints )?
     * This method handles both normal and special cases.
     * Normal case: Assume both breakpoints are closing to the Voronoi Vertex.
     * Special case: several sites that coincide, or to say, already on the same circle.
     */

    static
    boolean hasCircleEvent( EventSite current, EventSite other, double sweepY ) {
        // move the sweep line downwards a little bit
        // to move breakpoints as well.
        EventSite internalOffset = new EventSite( current, current.leftArc, current.rightArc, current.bisector );
        EventSite leafOffset = new EventSite( other, other.leftArc, other.rightArc, other.bisector );

        // update breakpoints after moving the sweep line
        // TODO: 1/16/2022 Precision issue.
        //  For test case: test_1/19,
        //  MyMath.EPSILON * 1000 is right, but MyMath.EPSILON * 10000 is wrong.
        final double OFFSET_Y = sweepY - MyMath.EPSILON * 1000;
        internalOffset.update( OFFSET_Y, true );
        leafOffset.update( OFFSET_Y, true );

        // get the intersection of the two voronoi edges
        Vector intersection = current.bisector.intersect( other.bisector )[ 0 ];
        // two bisectors are parallel
        if ( intersection == null ) return false;

        // Euclidean distance between the breakpoint and intersection becomes smaller
        // after the sweep line move downwards a little bit?
        double disOrigin1 = current.subtract( intersection ).lengthSq();
        double disOffset1 = internalOffset.subtract( intersection ).lengthSq();
        double disOrigin2 = other.subtract( intersection ).lengthSq();
        double disOffset2 = leafOffset.subtract( intersection ).lengthSq();

        // yes, have a circle event here.
        // no, no circle event here.
        boolean res1 = MyMath.doubleCompare( disOrigin1, disOffset1 ) > 0;
        boolean res2 = MyMath.doubleCompare( disOrigin2, disOffset2 ) > 0;
        // degenerate case: there are event points that coincide.
        // i.e. at least one breakpoint is closing to the Voronoi vertex.
        // and it's impossible that one endpoint is go away from a Voronoi vertex,
        // and the other is closing to it and no circle event here.
        // Because all breakpoints closing to the vertex will be merged
        // before encountering the impossible circumstance.
        // Or to say, both breakpoints are closing to the Voronoi Vertex.
        // TODO: 2/5/2022 is this right for sure?
        return res1 || res2;
    }

    /**
     * is there a degenerate circle event between those two triples( breakpoints )?
     * This method handles degenerate cases. i.e. a site overlapping a circle event.
     * also meaning several sites that coincide.
     */

    static
    boolean hasCircleEvent( Vector pi, Vector pre1, Vector pre2 ) {
        // check to see if the circle event is on the site event.
        // notice this is a degenerate case.
        Circle circle = Circles.getCircleByThreePoints( pi, pre1, pre2 );
        // three sites of the triple are on the same line.
        if ( circle == null ) return false;

        Vector lowest = circle.getLowest();
        return lowest.equalsXAndY( pi );
    }

    private String toStringInternalWithPoint() {
        return "I(" + x + "," + y + "): <" + ( leftArc.ID + 1 ) + ", " + ( rightArc.ID + 1 ) + ">";
    }

    private String toStringInternal() {
        return "I: <" + ( leftArc.ID + 1 ) + ", " + ( rightArc.ID + 1 ) + ">";
    }

    @Override
    public String toString() {
        return switch ( type ) {
            case SITE -> "S: " + ( siteFace.ID + 1 );
            case CIRCLE -> "C: " + super.toString() + " { " + circle + " }";
            case INTERNAL -> toStringInternal();
            case LEAF -> "L: " + ( siteFace.ID + 1 );
            case SPECIAL -> "Spe: " + toStringInternal().substring( 3 );
        };
    }
}
