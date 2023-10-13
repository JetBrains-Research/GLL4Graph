package benchmark;

import iguana.utils.input.GraphInput;
import org.apache.commons.cli.ParseException;
import org.iguana.grammar.Grammar;
import org.iguana.parser.IguanaParser;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

public class GraphBenchmark {
    private static final String RESULTS_DIR = "results";

    public static void main(String[] args) throws IOException, InterruptedException {
        final var cliParser = new CliParser();
        if (cliParser.hasHelp(args)) {
            cliParser.printHelp();
            return;
        }
        BenchmarkSettings settings = null;
        try {
            settings = BenchmarkSettings.parseCli(cliParser.parseArgs(args));
        } catch (ParseException e) {
            System.err.println(e.getMessage());
            cliParser.printHelp();
            System.exit(1);
        }
        final var benchmark = new GraphBenchmark(settings);
        benchmark.benchmark();
    }

    private final BenchmarkGraphStorage graphStorage;
    private final BenchmarkProblem problem;
    private final BenchmarkScenario scenario;
    private final String datasetName;
    private final String grammarPath;
    private final String graphPath;
    private final int warmupIterations;
    private final int measurementIterations;
    private final CsvGenerator csvGenerator;

    private GraphBenchmark(BenchmarkSettings settings) {
        graphStorage = BenchmarkGraphStorage.createBenchmarkStorage(settings.getStorageType());
        problem = BenchmarkProblem.createBenchmarkProblem(settings.getProblem());
        scenario = BenchmarkScenario.createBenchmarkScenario(settings.getScenario());
        datasetName = settings.getDatasetName();
        grammarPath = settings.getGrammarPath();
        graphPath = settings.getGraphPath();
        warmupIterations = settings.getWarmupIterations();
        measurementIterations = settings.getMeasurementIterations();
        csvGenerator = settings.isWithMemoryUsage()
                ? new CsvWithMemoryGenerator(scenario, RESULTS_DIR, datasetName, problem.toString(), graphStorage.toString())
                : new CsvGenerator(scenario, RESULTS_DIR, datasetName, problem.toString(), graphStorage.toString());
    }

    void benchmark() throws IOException, InterruptedException {
        MemoryCleaner memoryCleaner = new MemoryCleaner();
        Grammar grammar = Grammar.load(grammarPath, "json");
        graphStorage.loadGraph(graphPath);
        File outFile = new File(csvGenerator.getFilename());
        boolean fileExists = !outFile.createNewFile();
        System.out.printf("Benchmarking %s for %s graph%n", problem, datasetName);
        try (PrintWriter outStatsTime = new PrintWriter(new FileOutputStream(outFile, true), true)) {
            if (!fileExists) {
                outStatsTime.println(csvGenerator.getHeader());
            }

            final int maxIters = warmupIterations + measurementIterations;
            for (int iter = 0; iter < maxIters; ++iter) {

                final var chunks = scenario.getStartVerticesChunks();
                for (int chunkIndex = 0; chunkIndex < chunks.size(); chunkIndex++) {
                    graphStorage.onIterationStart();
                    List<Integer> chunk = chunks.get(chunkIndex);

                    final long stepStartPrepareTime = System.nanoTime();
                    IguanaParser parser = new IguanaParser(grammar);
                    GraphInput input = graphStorage.getGraphInput(chunk.stream());
                    final long stepStartTime = System.nanoTime();
                    final double stepPrepareTime = (double) (stepStartTime - stepStartPrepareTime) / 1_000_000_000.;
                    problem.runAlgo(parser, input);
                    final long stepStopTime = System.nanoTime();
                    final double stepRunTime = (double) (stepStopTime - stepStartTime) / 1_000_000_000.;

                    if (iter >= warmupIterations) {
                        outStatsTime.println(csvGenerator.getRecord(chunkIndex, stepPrepareTime, stepRunTime, problem.getResult()));
                        outStatsTime.flush();
                    }
                    problem.clearResult();
                    graphStorage.onIterationFinish();
                    parser = null;
                    input = null;
                    chunk = null;
                    memoryCleaner.freeMemory();
                }

            }
        }
        graphStorage.close();
    }

}