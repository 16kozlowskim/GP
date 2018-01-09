import java.util.Random;
import java.util.concurrent.*;
import java.util.List;
import java.util.ArrayList;
import java.io.*;

public class GeneticProgram implements Callable<Robot> {

  static Robot[] parent;
  static int maxGen;
  static int popSize;
  static double[] fitnesses;
  static Random rng = new Random();
  static Robot[] child;
  static String pathToRobocode;
  private int counter;

  public GeneticProgram(int counter) {
    this.counter = counter;
  }

  public static void initializeRobot() {
    parent = new Robot[popSize];
    child = new Robot[popSize];

    for (int i = 0; i < popSize; i++) {
      parent[i] = new Robot(i + 1, 1, pathToRobocode);
      parent[i].initialize();
    }
  }

  public static void main(String[] args) throws InterruptedException, ExecutionException, IOException {

    popSize = Integer.parseInt(args[0]);
    maxGen = Integer.parseInt(args[1]);
    pathToRobocode = args[2];

    try {
      Process p = Runtime.getRuntime().exec("rm -rf " + pathToRobocode + "/robots/evolving");
      p.waitFor();
      p =  Runtime.getRuntime().exec("mkdir " + pathToRobocode + "/robots/evolving");
      p.waitFor();
    } catch (Exception e) {
      System.out.println(e.getMessage());
    }

    initializeRobot();

    String[] robotNames = new String[popSize];

    BattleRunner battle = new BattleRunner(pathToRobocode);

    for (int genCount = 1; genCount <= maxGen; genCount++) {

      System.out.println("Beginning evolution of generation " + genCount);

      Runnable[] task = new Runnable[popSize];

      for (int i = 0; i < popSize; i++) {
        task[i] = () -> {
          parent[i].createFile();
          robotNames[i] = "evolving." + parent[i].name + "*";
        }
        new Thread(task[i]).start();
      }

      fitnesses = battle.fight(robotNames, genCount);
      /*
      ProcessBuilder[] pb = new ProcessBuilder[popSize];
      Process[] process = new Process[popSize];

      for (int i = 0; i < popSize; i++) {
        pb[i] = new ProcessBuilder(new String[] {"java", "-cp", pathToRobocode, "/libs/robocode.jar:.", "BattleRunner", robotNames[i], pathToRobocode, "" + genCount});
        pb[i].redirectErrorStream(true);
        process[i] = pb[i].start();
      }
      System.exit(0);
      fitnesses = new double[2 * popSize];

      for (int i = 0; i < popSize; i++) {
        process[i].waitFor();
        BufferedReader reader = new BufferedReader(new InputStreamReader(process[i].getInputStream()));
        StringBuilder builder = new StringBuilder();
        String line = null;
        while ( (line = reader.readLine()) != null) {
          builder.append(line);
        }
        String[] temp = builder.toString().split(",");
        System.out.println(temp[0] + ", " + temp[1]);
        //fitnesses[i] = Double.parseDouble(temp[0]);
        //fitnesses[i + popSize] = Double.parseDouble(temp[1]);
      }
      System.exit(0);
      */

      extractTop();

      List<Callable<Robot>> callableList = new ArrayList<Callable<Robot>>();

      for (int i = 2; i < popSize; i++) {
        Callable<Robot> callable = new GeneticProgram(i);
        callableList.add(callable);
      }

      ExecutorService executor = Executors.newFixedThreadPool(popSize - 2);

      System.out.println("Creating new generation...");

      List<Future<Robot>> futureObjects = executor.invokeAll(callableList);

      executor.shutdown();
      while (!executor.isTerminated()) {
        TimeUnit.SECONDS.sleep(1);
      }

      parent[0] = child[0];
      parent[1] = child[1];

      for (int i = 0; i < popSize - 2; i++) {
        parent[i + 2] = futureObjects.get(i).get();
      }
    }
  }

  @Override
  public Robot call() throws InterruptedException {
    int index1 = tournamentSelect1();
    int index2 = tournamentSelect2();
    int index3 = tournamentSelect3();
    return parent[index1].clone(parent[index1].ID, 0).geneticOp(parent[index2].clone(parent[index2].ID, 0), parent[index3].clone(parent[index3].ID, 0), counter + 1);
  }


  public static void extractTop() {
    double fitness = 0;
    double fitness2 = 0;
    int index1 = 0;
    int index2 = 0;

    for (int i = 0; i < popSize; i++) {
      if (fitness < fitnesses[i] + fitnesses[i + popSize]) {
        fitness = fitnesses[i] + fitnesses[i + popSize];
        index1 = i;
      } else if (fitness2 < fitnesses[i] + fitnesses[i + popSize]) {
        fitness2 = fitnesses[i] + fitnesses[i + popSize];
        index2 = i;
      }
    }
    child[0] = parent[index1].clone(1, 1);
    child[1] = parent[index2].clone(2, 1);

    System.out.println(parent[index1].name + " got the best score: " + fitnesses[index1]);
  }

  public static int tournamentSelect1() {
    double fitness = 0;
    int index;
    int best = -1;
    for (int i = 0; i < 5; i++) {
      index = rng.nextInt(popSize);
      if (fitness < fitnesses[index] + fitnesses[index + popSize]) {
        fitness = fitnesses[index] + fitnesses[index + popSize];
        best = index;
      }
    }
    return best;
  }

  public static int tournamentSelect2() {
    double fitness = 0;
    int index;
    int best = -1;
    for (int i = 0; i < 5; i++) {
      index = rng.nextInt(popSize);
      if (fitness < fitnesses[index]) {
        fitness = fitnesses[index];
        best = index;
      }
    }
    return best;
  }

  public static int tournamentSelect3() {
    double fitness = 0;
    int index;
    int best = -1;
    for (int i = 0; i < 5; i++) {
      index = rng.nextInt(popSize);
      if (fitness < fitnesses[index + popSize]) {
        fitness = fitnesses[index + popSize];
        best = index;
      }
    }
    return best;
  }
}
