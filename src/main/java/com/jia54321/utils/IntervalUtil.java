package com.jia54321.utils;

import java.util.*;

/**
 * java diff Interval(区间)
 */
public class IntervalUtil {
    public static enum Type { base, target }

    public static class Interval {
        public long start;
        public long end;
        public Type type;
        public String id;

        public Interval(long start, long end, Type type) {
            this(start, end, type, type.name());
        }

        public Interval(long start, long end, Type type, String id) {
            this.start = start;
            this.end = end;
            this.type = type;
            this.id = id;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            Interval interval = (Interval) o;

            if (start != interval.start) return false;
            if (end != interval.end) return false;
            if (type != interval.type) return false;
            if (id != null ? !id.equals(interval.id) : interval.id != null) return false;

            return true;
        }

        @Override
        public int hashCode() {
            int result = (int) (start ^ (start >>> 32));
            result = 31 * result + (int) (end ^ (end >>> 32));
            result = 31 * result + (type != null ? type.hashCode() : 0);
            result = 31 * result + (id != null ? id.hashCode() : 0);
            return result;
        }
    }
    public static class IntervalPartitionResult {
        /**
         * base Interval(区间)
         */
        public Interval base;
        /**
         * target Interval(区间)
         */
        public List<Interval> targets;
        /**
         * partition Interval(区间)
         */
        public List<Interval> partitions;

        public IntervalPartitionResult(Interval base, List<Interval> targets, List<Interval> result) {
            this.base = base;
            this.targets = targets;
            this.partitions = result;
        }

        public String desc() {
            StringBuffer sb = new StringBuffer();
            sb.append("原始[" + base.start + ", " + base.end + ")").append(" ");
            for (Interval target:  targets) {
                sb.append("区间[" + target.start + ", " + target.end + ") ");
            }
            sb.append("=>").append('\n');
            for (int i = 0; i < partitions.size(); i++) {
                Interval each = partitions.get(i);
                sb.append("    " + ( i + 1 ) + " ").append(each.id + "[" + each.start + "," + each.end + ") ").append('\n');
            }
            for (Interval each: partitions) {

            }
            return sb.deleteCharAt(sb.length()-1).toString();
        }
    }

    /**
     * partition
     * @param base
     * @param target
     * @return
     */
    private static List<Interval> part(Interval base, Interval target) {
        List<Interval> lst = new ArrayList<>();
        //
        Interval[] eachOnes = (Interval[])RangeUtil.idxCallback( target.start, ( idx ) -> {
            if ( idx <= 0f ) {
                return new Interval[]{ new Interval(base.start, base.end, target.type, target.id) };
            }
            if ( idx == 0.5f ) {
                return new Interval[] {
                        new Interval(  base.start, target.start, base.type, base.id),
                        new Interval(target.start, base.end, target.type, target.id)
                };
            }
            if ( idx >= 1f) {
                return new Interval[]{ new Interval(base.start, base.end, base.type, base.id) };
            }
            return new Interval[]{};
        }, base.start ,base.end );

        for (Interval each : eachOnes) {
            Interval[] eachTwos = (Interval[])RangeUtil.idxCallback( target.end, ( idx ) -> {
                if ( idx <= 0f ) {
                    return new Interval[]{ new Interval( each.start, each.end, base.type, base.id) };
                }
                if ( idx == 0.5f ) {
                    return new Interval[] {
                            new Interval(  each.start, target.end, each.type, each.id),
                            new Interval(target.end, each.end, base.type, base.id)
                    };
                }
                if ( idx >= 1f) {
                    return new Interval[]{ new Interval( each.start, each.end, each.type, each.id) };
                }
                return new Interval[]{};
            }, each.start ,each.end );

            for (Interval each2:  eachTwos) {
                lst.add(each2);
            }
        }
        return lst;
    }

    /**
     * partition
     * @param base
     * @param target
     * @return
     */
    private static List<Interval> partition(List<Interval> base, List<Interval> target) {
        for (int i = 0; i < target.size(); i++) {
            List <Interval> tmp = new ArrayList<>();
            for (Interval each: base ) {
                tmp.addAll(part(each, target.get(i)));
            }
            base = tmp;
        }
        return base;
    }

    public static IntervalPartitionResult partition(Interval base, Interval... target) {
        List <Interval> lst = new ArrayList<>();
        List <Interval> lstBase = new ArrayList<>();
        lstBase.add(base);
        List <Interval> lstTarget = new ArrayList<>(Arrays.asList(target));
        lst.addAll(partition(lstBase, lstTarget));

        return new IntervalPartitionResult(base, lstTarget, lst);
    }

}
