import java.io.PrintWriter
import java.io.File

object parsing {
  println("Welcome to the Scala worksheet")       //> Welcome to the Scala worksheet
  
  val source = scala.io.Source.fromFile("C:\\dev\\ws-gns-03\\lucene-hydroabstracts-scala\\misc\\glossary.csv")
                                                  //> source  : scala.io.BufferedSource = non-empty iterator
  val printer = new PrintWriter("C:\\dev\\ws-gns-03\\lucene-hydroabstracts-scala\\misc\\glossary_esc.csv")
                                                  //> printer  : java.io.PrintWriter = java.io.PrintWriter@4ab24098
  for (line <- source.getLines()) {
   
    val matcher = new scala.util.matching.Regex("^(\\d{4}),(.*?),(.*),(\\d{1})$", "id", "term", "text","num")

	  val thingy = for (matcher(id, term, text, num) <- matcher findAllIn line) yield (id, term, text, num)
	
	  val tlist = thingy.toList
	
	  for ((id, term, text, num) <- tlist) {
	
	   val newLine = s""""$id","$term","$text","$num""""
	    // println()
	    printer.write(newLine + "\n")
	  }
  }
  printer.flush()
  printer.close()
}