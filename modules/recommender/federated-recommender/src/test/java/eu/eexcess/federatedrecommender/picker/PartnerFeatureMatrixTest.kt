package eu.eexcess.federatedrecommender.picker

import org.junit.Test

import org.junit.Assert.*
import java.util.*

/**
 * Created by hziak on 09.12.15.
 */
class PartnerFeatureMatrixTest {

    @Test fun testGetData() {

    }

    @Test fun testSetData() {
        var partnerFeatureMatrix = PartnerFeatureMatrix(5,5)
        var arr = DoubleArray(1);
        arr.set(0,0.0)
        partnerFeatureMatrix.setRow(0,arr)
    }

    @Test fun testSetRow() {

    }
}