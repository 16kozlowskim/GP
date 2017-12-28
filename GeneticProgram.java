import java.util.Random;

public class GeneticProgram {

  static Robot[] parent;
  static int maxGen;
  static int popSize;
  static double[] fitnesses;
  static Random rng = new Random();
  static Robot[] child;


  public static void initializeRobot() {
    parent = new Robot[popSize];
    child = new Robot[popSize];

    for (int i = 0; i < popSize; i++) {
      parent[i] = new Robot(i + 1, 1);
      parent[i].initialize();
    }
  }

  public static void main(String[] args) {
    popSize = Integer.parseInt(args[0]);
    maxGen = Integer.parseInt(args[1]);
    initializeRobot();

    String[] robotNames = new String[popSize];

    BattleRunner battle = new BattleRunner();

    for (int genCount = 1; genCount <= maxGen; genCount++) {

      for (int i = 0; i < popSize; i++) {
        parent[i].createFile();
        robotNames[i] = "evolving." + parent[i].name + "*";
      }
      fitnesses = battle.createBattle(robotNames);
      for (int j = 0; j < fitnesses.length; j++) {
        System.out.println(fitnesses[j]);
      }

      extractTop();

      for (int i = 2 ; i < popSize; i++) {
        int index1 = tournamentSelect();
        int index2 = tournamentSelect();
        child[i] = parent[index1].geneticOp(parent[index2], i + 1);
      }
      for (int i = 0; i < popSize; i++) {
        parent[i] = child[i];
      }
    }
  }

  public static void extractTop() {
    double fitness = 0;
    double fitness2 = 0;
    int index;
    for (int i = 0; i < popSize; i++) {
      index = rng.nextInt(popSize);
      if (fitness < fitnesses[index]) {
        fitness = fitnesses[index];
        child[0] = parent[index];
      }
      else if (fitness2 < fitnesses[index]) {
        fitness2 = fitnesses[index];
        child[1] = parent[index];
      }
    }
    if (child[0] == null)
      child[0] = parent[rng.nextInt(popSize)];
    else
      child[0].update();

    if (child[1] == null)
      child[1] = parent[rng.nextInt(popSize)];
    else
      child[1].update();
  }

  public static int tournamentSelect() {
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
}
