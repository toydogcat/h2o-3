package hex.tree;

import org.apache.commons.lang.mutable.MutableInt;
import java.util.Map;
import java.util.TreeMap;

public class SplitValueHistogram  extends TreeMap<Double, MutableInt> {
    
    public void addValue(double splitValue, int count) {
        if (!this.containsKey(splitValue)) {
            this.put(splitValue, new MutableInt(0));
        }
        this.get(splitValue).add(count);
    }
    
    public void merge(TreeMap<Double, MutableInt> histogram) {
        for (Map.Entry<Double, MutableInt> entry: histogram.entrySet()) {
            this.addValue(entry.getKey(), entry.getValue().intValue());
        }
    }
}
