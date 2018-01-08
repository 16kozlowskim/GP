import java.util.Random;

public class GeneticProgram {

  static Robot[] parent;
  static int maxGen;
  static int popSize;
  static double[] fitnesses;
  static Random rng = new Random();
  static Robot[] child;
  static String pathToRobocode;


  public static void initializeRobot() {
    parent = new Robot[popSize];
    child = new Robot[popSize];

    for (int i = 0; i < popSize; i++) {
      parent[i] = new Robot(i + 1, 1, pathToRobocode);
      parent[i].initialize();
    }
  }

  public static void main(String[] args) {

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

      for (int i = 0; i < popSize; i++) {
        parent[i].createFile();
        robotNames[i] = "evolving." + parent[i].name + "*";
      }

      fitnesses = battle.fight(robotNames, genCount);

      extractTop();

      for (int i = 2; i < popSize; i++) {
        int index1 = tournamentSelect1();
        int index2 = tournamentSelect2();
        int index3 = tournamentSelect3();
        child[i] = parent[index1].geneticOp(parent[index2], parent[index3], i + 1);
        System.out.println("Done: " + i);
      }
      for (int i = 0; i < popSize; i++) {
        parent[i] = child[i];
      }
    }
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
    child[0] = parent[index1].clone(1);
    child[1] = parent[index2].clone(2);

    System.out.println(parent[index1].name + ", " + fitnesses[index1]);
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
