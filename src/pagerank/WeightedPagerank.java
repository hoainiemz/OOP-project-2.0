package pagerank;

import java.util.ArrayList;
import java.util.Arrays;

public class WeightedPagerank {

    private void formalize_bias(double [] biased) // formalize the biased so that the total is 1
    {
        double sum = 0;
        for (double num : biased)
        {
            sum += num;
        }
        for (int i = 0; i < biased.length; ++i)
        {
            biased[i] = biased[i]/sum;
        }
    }


    public double[] pagerank(int epoch_number, int n, ArrayList<DirectedEdge> adj[], double c, double [] biased)
    {
        //epoch_number: number of times running the algorithm
        //n:  number of nodes
        //adj: adjacent  list
        //c: fly out probability - probability going to a random node
        //biased: tendency when flying out - default is 1/n for all


        //formula: p_new = c B p_old + (1 - c) biased
        formalize_bias(biased); // make sure sum of biased is 1

        double[] resPoint = new double[n + 1]; // the answer - the point for each node
        double[] tempPoint = new double[n + 1]; // temp array when calculate - optimize
        double [] sumWeightOut = new double[n + 1]; // sumWeightOut[u] = total weight of edges directed from u

        for (int i = 1; i <= n; ++ i)
        {
            resPoint[i] = (double)1/n;
        }
        // Initialize: 1/n;
        Arrays.fill(sumWeightOut, 0);
        for (int u = 1; u <= n; ++ u)
        {
            for (DirectedEdge e : adj[u])
            {
                sumWeightOut[u] += e.w;
            }
        }
        for (int epoch = 1; epoch <= epoch_number; ++ epoch)
        {
            for (int u = 1; u <= n; ++ u)
            {
                tempPoint[u] = (1 - c) * biased[u];
            }
            for (int u = 1; u <= n; ++ u)
            {
                for (DirectedEdge e : adj[u])
                {
                    tempPoint[e.v] += c * resPoint[u] * e.w/sumWeightOut[u];
                }
            }
            System.arraycopy(tempPoint, 0, resPoint, 0, n + 1);
        }
        return resPoint;
    }
}
