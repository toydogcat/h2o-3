package hex.tree;
import hex.genmodel.algos.tree.SharedTreeNode;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Implementors of this interface have feature interactions calculation implemented.
 */
public interface FeatureInteractionsCollector {
    
    Map<String, FeatureInteraction> getFeatureInteractions(int maxInteractionDepth, int maxTreeDepth, int maxDeepening);

    void CollectFeatureInteractions(SharedTreeNode node, List<SharedTreeNode> interactionPath,
                                    double currentGain, double currentCover, double pathProba, int depth, int deepening,
                                    Map<String, FeatureInteraction> featureInteractions, Set<String> memo, 
                                    int maxInteractionDepth, int maxTreeDepth, int maxDeepening, int treeIndex);

}
