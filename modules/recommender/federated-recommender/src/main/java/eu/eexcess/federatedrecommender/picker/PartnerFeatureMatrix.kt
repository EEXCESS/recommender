package eu.eexcess.federatedrecommender.picker

import java.util.*

/**
 * Created by hziak on 09.12.15.
 */
public class PartnerFeatureMatrix (width: Int,height: Int) {
    var data = Array(height, { DoubleArray(width) })

    fun setRow(row: Int, rowValues: DoubleArray) {
//        System.out.println("set "+data.size.toString() +" "+ data.first().size +" " + data.last().size)
//        System.out.println("Setting row:"+row+ " Setting Values:"+rowValues)
        data.set(row, rowValues)
    }
    fun getMatrix(): Array<DoubleArray> {
//        System.out.println("get" +data.size.toString() +" "+ data.first().size +" " + data.last().size)
//        data.forEach { element ->
//            element.forEach { e -> System.out.print(e.toString() + " ") }
//            System.out.println("")
//        }
            return this.data
    }

}