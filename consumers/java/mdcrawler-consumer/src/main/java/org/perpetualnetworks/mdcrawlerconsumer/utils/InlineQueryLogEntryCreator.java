package org.perpetualnetworks.mdcrawlerconsumer.utils;

import net.ttddyy.dsproxy.ExecutionInfo;
import net.ttddyy.dsproxy.QueryInfo;
import net.ttddyy.dsproxy.listener.logging.DefaultQueryLogEntryCreator;
import org.apache.openjpa.lib.jdbc.SQLFormatter;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;


public class InlineQueryLogEntryCreator extends DefaultQueryLogEntryCreator {
    static class StringAsIntegerComparator implements Comparator<String> {
        @Override
        public int compare(String left, String right) {
            // make null first
            if (left == null && right == null) {
                return 0;
            }
            if (left == null) {
                return -1; // right is greater
            }
            if (right == null) {
                return 1; // left is greater;
            }

            try {
                int leftInt = Integer.parseInt(left);
                int rightInt = Integer.parseInt(right);
                return Integer.compare(leftInt, rightInt);
            } catch (NumberFormatException e) {
                return left.compareTo(right);  // use String comparison
            }
        }
    }

    @Override
    protected String formatQuery(String query) {
        SQLFormatter sqlFormatter = new SQLFormatter();
        sqlFormatter.setMultiLine(true);
        sqlFormatter.setClauseIndent("      ");
        sqlFormatter.setWrapIndent("          ");
        sqlFormatter.setLineLength(30);
        return "\n" + sqlFormatter.prettyPrint(super.formatQuery(query)
                .replace("select ", " SELECT ")
                .replace("insert ", " INSERT ")
                .replace(" join ", " JOIN ")
                .replace(" as ", " AS "));
    }

    @Override
    protected void writeParamsEntry(StringBuilder sb, ExecutionInfo execInfo, List<QueryInfo> queryInfoList) {
        sb.append("\nParams:[");
        for (QueryInfo queryInfo : queryInfoList) {
            boolean firstArg = true;
            for (Map<String, Object> paramMap : queryInfo.getQueryArgsList()) {

                if (!firstArg) {
                    sb.append(", ");
                } else {
                    firstArg = false;
                }

                SortedMap<String, Object> sortedParamMap = new TreeMap<>(new StringAsIntegerComparator());
                sortedParamMap.putAll(paramMap);

                sb.append("(");
                boolean firstParam = true;
                for (Map.Entry<String, Object> paramEntry : sortedParamMap.entrySet()) {
                    if (!firstParam) {
                        sb.append(", ");
                    } else {
                        firstParam = false;
                    }
                    Object parameter = paramEntry.getValue();
                    if (parameter != null && parameter.getClass().isArray()) {
                        sb.append(arrayToString(parameter));
                    } else {
                        sb.append(parameter);
                    }
                }
                sb.append(")");
            }
        }
        sb.append("]");
    }

    private String arrayToString(Object object) {
        if (object.getClass().isArray()) {
            if (object instanceof byte[]) {
                return Arrays.toString((byte[]) object);
            }
            if (object instanceof short[]) {
                return Arrays.toString((short[]) object);
            }
            if (object instanceof char[]) {
                return Arrays.toString((char[]) object);
            }
            if (object instanceof int[]) {
                return Arrays.toString((int[]) object);
            }
            if (object instanceof long[]) {
                return Arrays.toString((long[]) object);
            }
            if (object instanceof float[]) {
                return Arrays.toString((float[]) object);
            }
            if (object instanceof double[]) {
                return Arrays.toString((double[]) object);
            }
            if (object instanceof boolean[]) {
                return Arrays.toString((boolean[]) object);
            }
            if (object instanceof Object[]) {
                return Arrays.toString((Object[]) object);
            }
        }
        throw new UnsupportedOperationException("Array type not supported: " + object.getClass());
    }
}
