package org.opentripplanner.graph_builder

import org.opentripplanner.gtfs._
import org.opentripplanner.graph_builder.model._

import com.twitter.scalding._

import org.onebusaway.gtfs.impl._
import org.onebusaway.gtfs.services._
import org.opentripplanner.routing.edgetype.factory._

import java.io.File



class GraphBuilderJob(cacheDirUri: String, bundles:GtfsBundles) extends Job(Args("")) {

  val bundlePipe = IterableSource(bundles.getBundles()).read
  val processed = bundlePipe.map(0 -> 'bundle) { bundle:GtfsBundle =>
    //val h=TextLine("hfs://" + System.currentTimeMillis.toString)
    processBundle(bundle) }
  processed.write(SequenceFile("%s/final.out" format(cacheDirUri)))

  //////////////////////////////////////////////////////////
  val inputPath = "%s/hello.txt" format(cacheDirUri)
  val outputPath = "%s/output4.txt" format(cacheDirUri)

  val lines = TextLine(inputPath).read.debug
  val words = lines.flatMap('line -> 'word){ line : String => line.split("\\s")}
  val wordLengths = lines.flatMap('line -> 'length){ line : String => line.split("\\s").map { w => w.length } }

    val groupedWords = words.groupBy('word){group => group.size}
    val groupedLengths = wordLengths.groupBy('length) { group => group.size }

    groupedWords.debug.write(Tsv(outputPath))
    groupedLengths.debug.write(Tsv(outputPath + ".lengths"))

    def buildFlow = super.buildFlow(Mode.mode)


    def processBundle(bundle:GtfsBundle) = {
      val dao:GtfsMutableRelationalDao = new GtfsRelationalDaoImpl()
//      val context:GtfsContext = GtfsLibrary.createContext(dao, service)
//      val hf:GTFSPatternHopFactory = new GTFSPatternHopFactory(context)
//      hf.setStopContext(stopContext)
//      hf.setFareServiceFactory(_fareServiceFactory)
//      hf.setMaxStopToShapeSnapDistance(gtfsBundle.getMaxStopToShapeSnapDistance())

//      if (generateFeedIds && gtfsBundle.getDefaultAgencyId() == null) {
//        gtfsBundle.setDefaultAgencyId("FEED#" + bundleIndex)
//      }

      //loadBundle(gtfsBundle, dao);
      "bundle!"
    }
}
