package com.jia54321.utils;

import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;
import static com.jia54321.utils.IntervalUtil.*;

/**
 * 测试用例
 */
public class IntervalUtilTest {

    @Test
    public void test_partition() {
        Interval month1 = new Interval(30, 60, Type.target, "月卡id1");
        Interval month2 = new Interval(90, 120, Type.target, "月卡id2");
        Interval interval = null;
        IntervalPartitionResult p = null;

        // case 1
        interval = new Interval(10, 20, Type.base, "临时");
        p = partition( interval, month1, month2 );
        System.out.println( "case 1 " + p.desc());
        assertTrue( p.partitions.get(0).equals( new Interval(10, 20, interval.type, interval.id) ) );


        // case 2
        interval = new Interval(10, 40, Type.base, "临时");
        p = partition(interval, month1, month2);
        System.out.println( "case 2 " + p.desc());
        assertTrue( 2 == p.partitions.size() );
        assertTrue( p.partitions.get(0).equals( new Interval(10, 30, interval.type, interval.id) ));
        assertTrue( p.partitions.get(1).equals( new Interval(30, 40, month1.type, month1.id) ) );


        // case 3
        interval = new Interval(10, 70, Type.base, "临时");
        p = partition(interval, month1, month2);
        System.out.println( "case 3 " + p.desc());
        assertTrue( 3 == p.partitions.size() );
        assertTrue( p.partitions.get(0).equals( new Interval(10, 30, interval.type, interval.id) ));
        assertTrue( p.partitions.get(1).equals( new Interval(30, 60, month1.type, month1.id) ) );
        assertTrue( p.partitions.get(2).equals( new Interval(60, 70, interval.type, interval.id) ) );


        // case 4
        interval = new Interval(10, 100, Type.base, "临时");
        p = partition(interval, month1, month2);
        System.out.println( "case 4 " + p.desc());
        assertTrue( 4 == p.partitions.size() );
        assertTrue( p.partitions.get(0).equals( new Interval(10, 30, interval.type, interval.id) ));
        assertTrue( p.partitions.get(1).equals( new Interval(30, 60, month1.type, month1.id) ) );
        assertTrue( p.partitions.get(2).equals( new Interval(60, 90, interval.type, interval.id) ) );
        assertTrue( p.partitions.get(3).equals( new Interval(90, 100, month2.type, month2.id) ) );


        // case 5
        interval = new Interval(10, 130, Type.base, "临时");
        p = partition(interval, month1, month2);
        System.out.println( "case 5 " + p.desc());
        assertTrue( 5 == p.partitions.size() );
        assertTrue( p.partitions.get(0).equals( new Interval(10, 30, interval.type, interval.id) ));
        assertTrue( p.partitions.get(1).equals( new Interval(30, 60, month1.type, month1.id) ) );
        assertTrue( p.partitions.get(2).equals( new Interval(60, 90, interval.type, interval.id) ) );
        assertTrue( p.partitions.get(3).equals( new Interval(90, 120, month2.type, month2.id) ) );
        assertTrue( p.partitions.get(4).equals( new Interval(120, 130, interval.type, interval.id) ) );


        // case 6
        interval = new Interval(30, 40, Type.base, "临时");
        p = partition(interval, month1, month2);
        System.out.println( "case 6 " + p.desc());
        assertTrue( 1 == p.partitions.size() );
        assertTrue( p.partitions.get(0).equals( new Interval(30, 40, month1.type, month1.id) ) );

        // case 7
        interval = new Interval(30, 70, Type.base, "临时");
        p = partition(interval, month1, month2);
        System.out.println( "case 7 " + p.desc());
        assertTrue( 2 == p.partitions.size() );
        assertTrue( p.partitions.get(0).equals( new Interval(30, 60, month1.type, month1.id) ) );
        assertTrue( p.partitions.get(1).equals( new Interval(60, 70, interval.type, interval.id) ) );

        // case 8
        interval = new Interval(30, 100, Type.base, "临时");
        p = partition(interval, month1, month2);
        System.out.println( "case 8 " + p.desc());
        assertTrue( 3 == p.partitions.size() );
        assertTrue( p.partitions.get(0).equals( new Interval(30, 60, month1.type, month1.id) ) );
        assertTrue( p.partitions.get(1).equals( new Interval(60, 90, interval.type, interval.id) ) );
        assertTrue( p.partitions.get(2).equals( new Interval(90, 100, month2.type, month2.id) ) );

        // case 9
        interval = new Interval(30, 130, Type.base, "临时");
        p = partition(interval, month1, month2);
        System.out.println( "case 9 " + p.desc());
        assertTrue( 4 == p.partitions.size() );
        assertTrue( p.partitions.get(0).equals( new Interval(30, 60, month1.type, month1.id) ) );
        assertTrue( p.partitions.get(1).equals( new Interval(60, 90, interval.type, interval.id) ) );
        assertTrue( p.partitions.get(2).equals( new Interval(90, 120, month2.type, month2.id) ) );
        assertTrue( p.partitions.get(3).equals( new Interval(120, 130, interval.type, interval.id) ) );


        // case 10
        interval = new Interval(60, 70, Type.base, "临时");
        p = partition(interval, month1, month2);
        System.out.println( "case 10 " + p.desc());
        assertTrue( 1 == p.partitions.size() );
        assertTrue( p.partitions.get(0).equals( new Interval(60, 70, interval.type, interval.id) ) );

        // case 11
        interval = new Interval(60, 100, Type.base, "临时");
        p = partition(interval, month1, month2);
        System.out.println( "case 11 " + p.desc());
        assertTrue( 2 == p.partitions.size() );
        assertTrue( p.partitions.get(0).equals( new Interval(60, 90, interval.type, interval.id) ) );
        assertTrue( p.partitions.get(1).equals( new Interval(90, 100, month2.type, month2.id) ) );

        // case 12
        interval = new Interval(60, 130, Type.base, "临时");
        p = partition(interval, month1, month2);
        System.out.println( "case 12 " + p.desc());
        assertTrue( 3 == p.partitions.size() );
        assertTrue( p.partitions.get(0).equals( new Interval(60, 90, interval.type, interval.id) ) );
        assertTrue( p.partitions.get(1).equals( new Interval(90, 120, month2.type, month2.id) ) );
        assertTrue( p.partitions.get(2).equals( new Interval(120, 130, interval.type, interval.id) ) );


        // case 13
        interval = new Interval(90, 100, Type.base, "临时");
        p = partition(interval, month1, month2);
        System.out.println( "case 13 " + p.desc());
        assertTrue( 1 == p.partitions.size() );
        assertTrue( p.partitions.get(0).equals( new Interval(90, 100, month2.type, month2.id) ) );


        // case 14
        interval = new Interval(90, 130, Type.base, "临时");
        p = partition(interval, month1, month2);
        System.out.println( "case 14 " + p.desc());
        assertTrue( 2 == p.partitions.size() );
        assertTrue( p.partitions.get(0).equals( new Interval(90, 120, month2.type, month2.id) ) );
        assertTrue( p.partitions.get(1).equals( new Interval(120, 130, interval.type, interval.id) ) );


        // case 15
        interval = new Interval(120, 130, Type.base, "临时");
        p = partition(interval, month1, month2);
        System.out.println( "case 15 " + p.desc());
        assertTrue( 1 == p.partitions.size() );
        assertTrue( p.partitions.get(0).equals( new Interval(120, 130, interval.type, interval.id) ) );
    }
}
