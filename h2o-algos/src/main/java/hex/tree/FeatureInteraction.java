package hex.tree;

import hex.genmodel.algos.tree.SharedTreeNode;
import java.util.Comparator;
import java.util.List;

public class FeatureInteraction {

    public String name;
    public int depth;
    public double gain;
    public double cover;
    public double FScore;
    public double FScoreWeighted;
    public double averageFScoreWeighted;
    public double averageGain;
    public double expectedGain;
    public double treeIndex;
    public double treeDepth;
    public double averageTreeIndex;
    public double averageTreeDepth;

    public boolean hasLeafStatistics;
    public double sumLeafValuesLeft;
    public double sumLeafCoversLeft;
    public double sumLeafValuesRight;
    public double sumLeafCoversRight;
    
    public SplitValueHistogram splitValueHistogram;
    
    public FeatureInteraction(List<SharedTreeNode> interactionPath, double gain, double cover, double pathProba, double depth, double FScore, double treeIndex) {
        this.name = InteractionPathToStr(interactionPath, false, true);
        this.depth = interactionPath.size() - 1;
        this.gain = gain;
        this.cover = cover;
        this.FScore = FScore;
        this.FScoreWeighted = pathProba;
        this.averageFScoreWeighted = this.FScoreWeighted / this.FScore;
        this.averageGain = this.gain / this.FScore;
        this.expectedGain = this.gain * pathProba;
        this.treeIndex = treeIndex;
        this.treeDepth = depth;
        this.averageTreeIndex = this.treeIndex / this.FScore;
        this.averageTreeDepth = this.treeDepth / this.FScore;
        this.hasLeafStatistics = false;
        this.splitValueHistogram = new SplitValueHistogram();
        
        if (this.depth == 0) {
            splitValueHistogram.addValue(interactionPath.get(0).getSplitValue(), 1);
        }
    }

    public static String InteractionPathToStr(final List<SharedTreeNode> interactionPath, final boolean encodePath, final boolean sortByFeature) {
        if (sortByFeature && !encodePath) {
            interactionPath.sort(Comparator.comparing(SharedTreeNode::getColName));
        }
        
        StringBuilder sb = new StringBuilder();
        String delim = encodePath ? "-" : "|";
        
        for (SharedTreeNode node : interactionPath) {
            if (node != interactionPath.get(0)) {
                sb.append(delim);
            }
            sb.append(encodePath ? node.getNodeNumber() : node.getColName());
        }
        
        return sb.toString();
    }
}
