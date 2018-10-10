import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import heros.IFDSTabulationProblem;
import heros.InterproceduralCFG;
import soot.*;
import soot.jimple.*;
import soot.jimple.toolkits.ide.DefaultJimpleIFDSTabulationProblem;
import soot.jimple.toolkits.ide.JimpleIFDSSolver;
import soot.jimple.toolkits.ide.icfg.JimpleBasedInterproceduralCFG;
import soot.options.Options;

public class Stub2 {

	public static final boolean DEBUG = true;

	public static void main(String[] args) {
		if (args.length < 2) {
			System.out.println("Usage: java " + Stub2.class.getCanonicalName() + " <classpath> <main-classes>");
			System.exit(1);
		}

		configure(args[0]);
		registerEntryPoints(args[1].split(":"));

		PackManager.v().getPack("wjtp").add(new Transform("wjtp.myanalysis", new MySceneTransformer()));
		PackManager.v().runPacks();
	}

	public static void
	registerEntryPoints(String[] entrypoints) {
		 List<SootMethod> entry_points = new ArrayList<>();
		 if (entrypoints.length == 0) {
			 throw new RuntimeException("No entry points specified");
		 }
		 if (DEBUG) {
			 System.out.println("Entry points (" + entrypoints.length + "):");
		 }
		 for (String entrypoint : entrypoints) {
			 SootClass c = Scene.v().forceResolve(entrypoint, SootClass.BODIES);
			 c.setApplicationClass();
			 Scene.v().loadNecessaryClasses();
			 SootMethod method = c.getMethodByName("main");
			 entry_points.add(method);
			 if (DEBUG) {
				 System.out.println("  " + method);
			 }
		 }
		 Scene.v().setEntryPoints(entry_points);
	}

	/**
	 * Configures Soot for whole-program analysis of a given classpath
	 *
	 * @param classpath
	 */
	public static void configure(String classpath) {
		Options.v().set_verbose(false);
        Options.v().set_keep_line_number(true);
        Options.v().set_src_prec(Options.src_prec_class);
        Options.v().set_soot_classpath(classpath);
        Options.v().set_prepend_classpath(true);

        Options.v().set_output_format(Options.output_format_none); // or _xml?
        PhaseOptions.v().setPhaseOption("bb", "off");
        PhaseOptions.v().setPhaseOption("bop", "off");
        PhaseOptions.v().setPhaseOption("db", "off");
        PhaseOptions.v().setPhaseOption("gb", "off");
	//PhaseOptions.v().setPhaseOption("cg", "off");

        Options.v().set_whole_program(true); // whole-program analysis

        //setAdvancedCallgraph(false);
	}

	/**
	 * Switches from CHA to RTA or VTA callgraph analysis
	 *
	 * By default, Soot uses Class Hierarchy Analysis (CHA) to build call graphs.
	 * This method enables Rapid Type Analysis (RTA) or Variable Type Analysis (VTA).
	 *
	 * @param vta
	 */
	public static void
	setAdvancedCallgraph(boolean vta) {
		// Enable SPARK framework
		PhaseOptions.v().setPhaseOption("cg.cha", "enabled:false");
		PhaseOptions.v().setPhaseOption("cg.spark", "enabled:true");
		PhaseOptions.v().setPhaseOption("cg.spark", "on-fly-cg:false"); // disabled Spark's advanced analysis
		PhaseOptions.v().setPhaseOption("cg.spark", (vta ? "vta" : "rta") + ":true");
	}

	/**
	 * Iterate over all statements in the loaded Scene (i.e., the program and all dependencies) 
	 *
	 * @param tester
	 */
	public static void
	printAll(Function<Stmt, String> tester) {
		for (SootClass cl : Scene.v().getClasses()) {
			System.out.println(cl);
			for (SootMethod m: cl.getMethods()) {
				if (m.hasActiveBody()) {
					System.out.println("  " + m);
					Body body = m.getActiveBody();
					for (Unit unit : body.getUnits()) {
						//final Unit unit = ub.getUnit();
						final String label = tester.apply((Stmt) unit);
						if (label != null) {
							System.out.println("  " + label + " " + m.toString() + " " + unit.getJavaSourceStartColumnNumber());
						}
					}
				}
			}
		}
	}

	public static abstract class MyIFDSTabulator extends DefaultJimpleIFDSTabulationProblem<Integer, InterproceduralCFG<Unit,SootMethod>> {
		/**
		 * Cosntructs an IFDS tabulator
		 * @param icfg
		 */
		public MyIFDSTabulator(InterproceduralCFG<Unit, SootMethod> icfg) {
			super(icfg);
		}
	}

	public static final class MySceneTransformer extends SceneTransformer {
		@Override
		protected void internalTransform(String phase_name, Map<String, String> options) {
			// Here is how you can get a SootMethod by name:
			// This call will raise an ex
			SootMethod m = Scene.v().getMethod("<java.util.Collection: boolean contains(java.lang.Object)>");

//			IFDSTabulationProblem<Unit, Local ,SootMethod,InterproceduralCFG<Unit,SootMethod>> problem =
//					new MyIFDSTabulator(new JimpleBasedInterproceduralCFG());
//			JimpleIFDSSolver<Local,InterproceduralCFG<Unit,SootMethod>> solver = new JimpleIFDSSolver<>(problem);
//			solver.solve();
			printAll(new Function<Stmt, String>() {
				@Override
				public String apply(Stmt stmt) {
					return null;
				}
			});
		}
	}
}