package hex.tree;

import hex.genmodel.algos.tree.SharedTreeNode;

import java.util.Comparator;
import java.util.List;
import java.util.Map;

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
    
    public FeatureInteraction(List<SharedTreeNode> interactionPath, double gain, double cover, double pathProba, double depth, double FScore) {
        this.name = InteractionPathToStr(interactionPath, false, true);
        this.depth = interactionPath.size() - 1;
        this.gain = gain;
        this.cover = cover;
        this.FScore = FScore;
        this.FScoreWeighted = pathProba;
        this.averageFScoreWeighted = this.FScoreWeighted / this.FScore;
        this.averageGain = this.gain / this.FScore;
        this.expectedGain = this.gain * pathProba;
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
    
    public static void merge(Map<String, FeatureInteraction> leftFeatureInteractions, Map<String, FeatureInteraction> rightFeatureInteractions) {
        for (Map.Entry<String,FeatureInteraction> currEntry : rightFeatureInteractions.entrySet()) {
            if (leftFeatureInteractions.containsKey(currEntry.getKey())) {
                FeatureInteraction leftFeatureInteraction = leftFeatureInteractions.get(currEntry.getKey());
                FeatureInteraction rightFeatureInteraction = currEntry.getValue();
                leftFeatureInteraction.gain += rightFeatureInteraction.gain;
                leftFeatureInteraction.cover += rightFeatureInteraction.cover;
                leftFeatureInteraction.FScore += rightFeatureInteraction.FScore;
                leftFeatureInteraction.FScoreWeighted += rightFeatureInteraction.FScoreWeighted;
                leftFeatureInteraction.averageFScoreWeighted = leftFeatureInteraction.FScoreWeighted / leftFeatureInteraction.FScore;
                leftFeatureInteraction.averageGain = leftFeatureInteraction.gain / leftFeatureInteraction.FScore;
                leftFeatureInteraction.expectedGain += rightFeatureInteraction.expectedGain;
            } else {
                leftFeatureInteractions.put(currEntry.getKey(), currEntry.getValue());
            }
        }
    }
}
