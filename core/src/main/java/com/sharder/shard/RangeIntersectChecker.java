package com.sharder.shard;

import com.sharder.Token;
import com.sharder.shard.ShardDefinitionRange.ColumnRangeConditions;

/**
 * Check if two ranges intersect.
 */
public class RangeIntersectChecker {
    public static boolean intersects(ColumnRangeConditions conditions1, ColumnRangeConditions conditions2) {
        final Token condition1Left = conditions1.getLeft().getValue();
        final Token condition1Right = conditions1.getRight().getValue();
        final Token condition2Left = conditions2.getLeft().getValue();
        final Token condition2Right = conditions2.getRight().getValue();

        if (condition1Right.compareTo(condition2Left) <= 0) {
            if (condition1Right.compareTo(condition2Left) != 0) {
                return false;
            }
            return conditions1.getRight().includeValue() && conditions2.getLeft().includeValue();
        }

        if (condition1Left.compareTo(condition2Right) >= 0) {
            if (condition1Left.compareTo(condition2Right) != 0) {
                return false;
            }
            return conditions1.getLeft().includeValue() && conditions2.getRight().includeValue();
        }

        return true;
    }
}
