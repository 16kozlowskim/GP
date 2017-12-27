public class GeneticProgram {

  static Robot[] robotBatch;
  static int maxGen;
  static int popSize;


  public static void initializeRobot(int popSize, int maxGen) {
    robotBatch = new Robot[popSize];
    this.maxGen = maxGen;
    this.popSize = popSize;
    for (int i = 0; i < popSize; i++) {
      robotBatch[i] = new Robot(i + 1, 1);
      robotBatch[i].initialize();
    }
  }

  public static void main(String[] args) {
    initializeRobot(parseInt(args[0]), parseInt(args[1]));

    String[] robotNames = new String[popSize];

    BattleRunner battle = new BattleRunner();

    for (int genCount = 1; genCount <= maxGen; genCount++) {

      for (int i = 0; i < popSize; i++) {
        robotBatch[i].createFile();
        robotNames[i] = robotBatch[i].name;
      }
      double[] fitnesses = battle.createBattle(robotNames);




    }
  }
}
