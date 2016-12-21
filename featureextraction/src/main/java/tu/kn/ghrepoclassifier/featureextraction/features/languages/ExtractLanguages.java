package tu.kn.ghrepoclassifier.featureextraction.features.languages;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * Created by felix on 27.11.16.
 */
public class ExtractLanguages {
	static String [] top50ProgrammingLanguages = 
		{"JavaScript","Java","Python","CSS","PHP","Ruby","C++","C","Shell","C#","Objective-C","R",
			"VimL","Go","Perl","CoffeeScript","TeX","Swift","Scala","Emacs","Lisp","Haskell","Lua",
			"Clojure","Matlab","Arduino","Groovy","Puppet","Rust","PowerShell","Erlang","Visual Basic",
			"Processing","Assembly","TypeScript","XSLT","ActionScript","ASP","OCaml","D","Scheme","Dart",
			"Common Lisp","Julia","F#","Elixir","FORTRAN","Haxe","Racket","Logos"};
	
	static String [] allProgrammingLanguages = 
		{"1C Enterprise","ABAP","ABNF","AGS Script","AMPL","ANTLR","API Blueprint","APL","ASN.1","ASP",
			"ATS","ActionScript","Ada","Agda","Alloy","Alpine Abuild","Ant Build System","ApacheConf",
			"Apex","Apollo Guidance Computer","AppleScript","Arc","Arduino","AsciiDoc","AspectJ",
			"Assembly","Augeas","AutoHotkey","AutoIt","Awk","Batchfile","Befunge","Bison","BitBake",
			"Blade","BlitzBasic","BlitzMax","Bluespec","Boo","Brainfuck","Brightscript","Bro","C","C#",
			"C++","C-ObjDump","C2hs Haskell","CLIPS","CMake","COBOL","COLLADA","CSON","CSS","CSV",
			"Cap'n Proto","CartoCSS","Ceylon","Chapel","Charity","ChucK","Cirru","Clarion","Clean",
			"Click","Clojure","CoffeeScript","ColdFusion","ColdFusion CFC","Common Lisp",
			"Component Pascal","Cool","Coq","Cpp-ObjDump","Creole","Crystal","Csound","Csound Document",
			"Csound Score","Cucumber","Cuda","Cycript","Cython","D","D-ObjDump","DIGITAL Command Language",
			"DM","DNS Zone","DTrace","Darcs Patch","Dart","Diff","Dockerfile","Dogescript","Dylan","E",
			"EBNF","ECL","ECLiPSe","EJS","EQ","Eagle","Ecere Projects","Eiffel","Elixir","Elm",
			"Emacs Lisp","EmberScript","Erlang","F#","FLUX","FORTRAN","Factor","Fancy","Fantom",
			"Filebench WML","Filterscript","Formatted","Forth","FreeMarker","Frege","G-code","GAMS",
			"GAP","GAS","GCC Machine Description","GDB","GDScript","GLSL","Game Maker Language","Genshi",
			"Gentoo Ebuild","Gentoo Eclass","Gettext Catalog","Glyph","Gnuplot","Go","Golo","Gosu",
			"Grace","Gradle","Grammatical Framework","Graph Modeling Language","GraphQL","Graphviz (DOT)",
			"Groff","Groovy","Groovy Server Pages","HCL","HLSL","HTML","HTML+Django","HTML+ECR","HTML+EEX",
			"HTML+ERB","HTML+PHP","HTTP","Hack","Haml","Handlebars","Harbour","Haskell","Haxe","Hy",
			"HyPhy","IDL","IGOR Pro","INI","IRC log","Idris","Inform 7","Inno Setup","Io","Ioke",
			"Isabelle","Isabelle ROOT","J","JFlex","JSON","JSON5","JSONLD","JSONiq","JSX","Jade","Jasmin",
			"Java","Java Server Pages","JavaScript","Julia","Jupyter Notebook","KRL","KiCad","Kit","Kotlin",
			"LFE","LLVM","LOLCODE","LSL","LabVIEW","Lasso","Latte","Lean","Less","Lex","LilyPond","Limbo",
			"Linker Script","Linux Kernel Module","Liquid","Literate Agda","Literate CoffeeScript",
			"Literate Haskell","LiveScript","Logos","Logtalk","LookML","LoomScript","Lua","M","M4",
			"M4Sugar","MAXScript","MQL4","MQL5","MTML","MUF","Makefile","Mako","Markdown","Mask",
			"Mathematica","Matlab","Maven POM","Max","MediaWiki","Mercury","Metal","MiniD","Mirah",
			"Modelica","Modula-2","Module Management System","Monkey","Moocode","MoonScript","Myghty","NCL",
			"NL","NSIS","Nemerle","NetLinx","NetLinx+ERB","NetLogo","NewLisp","Nginx","Nimrod","Ninja",
			"Nit","Nix","Nu","NumPy","OCaml","ObjDump","Objective-C","Objective-C++","Objective-J",
			"Omgrofl","Opa","Opal","OpenCL","OpenEdge ABL","OpenRC runscript","OpenSCAD","Org","Ox",
			"Oxygene","Oz","PAWN","PHP","PLSQL","PLpgSQL","POV-Ray SDL","Pan","Papyrus","Parrot",
			"Parrot Assembly","Parrot Internal Representation","Pascal","Perl","Perl6","Pic","Pickle",
			"PicoLisp","PigLatin","Pike","Pod","PogoScript","Pony","PostScript","PowerBuilder","PowerShell",
			"Processing","Prolog","Propeller Spin","Protocol Buffer","Public Key","Puppet","Pure Data",
			"PureBasic","PureScript","Python","Python console","Python traceback","QML","QMake","R","RAML",
			"RDoc","REALbasic","REXX","RHTML","RMarkdown","RPM Spec","RUNOFF","Racket","Ragel in Ruby Host",
			"Raw token data","Rebol","Red","Redcode","Ren'Py","RenderScript","RobotFramework","Rouge","Ruby",
			"Rust","SAS","SCSS","SMT","SPARQL","SQF","SQL","SQLPL","SRecode Template","STON","SVG","Sage",
			"SaltStack","Sass","Scala","Scaml","Scheme","Scilab","Self","Shell","ShellSession","Shen","Slash",
			"Slim","Smali","Smalltalk","Smarty","SourcePawn","Squirrel","Stan","Standard ML","Stata","Stylus",
			"SubRip Text","Sublime Text Config","SuperCollider","Swift","SystemVerilog","TI Program","TLA",
			"TOML","TXL","Tcl","Tcsh","TeX","Tea","Terra","Text","Textile","Thrift","Turing","Turtle","Twig",
			"TypeScript","Unified Parallel C","Unity3D Asset","Uno","UnrealScript","UrWeb","VCL","VHDL",
			"Vala","Verilog","VimL","Visual Basic","Volt","Vue","Wavefront Material","Wavefront Object",
			"Web Ontology Language","WebIDL","World of Warcraft Addon Data","X10","XC","XML","XPages",
			"XProc","XQuery","XS","XSLT","Xojo","Xtend","YAML","YANG","Yacc","Zephir","Zimpl"};
	
	public static String extractProgrammingLanguages(Map<String, Long> languages) {
		String l = "";
		
		for (String p: allProgrammingLanguages) {
			if (languages.containsKey(p)) {
				l += "," + languages.get(p);
			} else {
				l += ",0";
			}
		}
		return l;
	}

	public static String extractProgrammingLanguagesFraction(Map<String, Long> languages) {
		String l = "";
		
		double sum = 0.0;
		for (Object ltotal: languages.values()) {
			sum += Double.parseDouble("" + ltotal);
		}

		for (String p: allProgrammingLanguages) {
			if (languages.containsKey(p)) {
				if (sum != 0.0) {
					l += "," + (Double.parseDouble("" + languages.get(p)) / sum);
				} else {
					l += ",0";
				}
			} else {
				l += ",0";
			}
		}
		return l;
	}

	public static List<String> getFeatureLabels() {
		List<String> features = new ArrayList();
		for (String p: allProgrammingLanguages) {
			features.add("lang_" + p.replace(' ','_'));
		}
		return features;
	}

	public static List<String> getFeatureLabelsFraction() {
		List<String> features = new ArrayList();
		for (String p: allProgrammingLanguages) {
			features.add("lang_fraction_" + p.replace(' ','_'));
		}
		return features;
	}
}
