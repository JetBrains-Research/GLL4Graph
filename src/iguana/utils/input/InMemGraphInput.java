package iguana.utils.input;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class InMemGraphInput extends GraphInput {
    private final List<List<Edge>> adjacencyList;
    private final Stream<Integer> startVertices;
    private final Stream<Integer> finalVertices;

    public InMemGraphInput(List<List<Edge>> adjacencyList, Stream<Integer> startVertices, Stream<Integer> finalVertices) {
        this.adjacencyList = adjacencyList;
        this.startVertices = startVertices;
        this.finalVertices = finalVertices;
    }

    @Override
    public Stream<Integer> getStartVertices() {
        return this.startVertices;
    }

    @Override
    public Stream<Integer> getFinalVertices() {
        return finalVertices;
    }

    @Override
    public boolean isFinal(int v) {
        return finalVertices.collect(Collectors.toList()).contains(v);
    }

    @Override
    public List<Integer> getDestVertex(int v, String t) {
        return adjacencyList.get(v).stream()
                .filter(edge -> edge.getTag().equals(t))
                .map(Edge::getDestVertex).collect(Collectors.toList());
    }

    @Override
    public Stream<Integer> nextSymbols(int v) {
        List<Integer> nextSymbols = new ArrayList<>();
        if (isFinal(v)) {
            nextSymbols.add(EOF);
        }
//        List<Integer> nextSymbols = adjacencyList.get(v).stream()
//                .map(edge -> (int) edge.getTag().charAt(0));
        for (Edge edge: adjacencyList.get(v)) {
            nextSymbols.add((int) edge.getTag().charAt(0));
        }
        return nextSymbols.stream();
    }

}
