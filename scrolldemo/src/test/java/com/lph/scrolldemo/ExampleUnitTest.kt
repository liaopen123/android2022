package com.lph.scrolldemo

import org.junit.Test

import org.junit.Assert.*

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {


    @Test
    fun addition_isCorrect() {
        assertEquals(4, 2 + 2)
    }

    @Test
    fun intersection(nums1: IntArray, nums2: IntArray): IntArray {
         var minNums: IntArray
         var maxNums: IntArray
         var numbers = ArrayList<Int>()
         var duplicateNumbers = ArrayList<Int>()
        if (nums1.size>nums2.size) {
            minNums = nums2
            maxNums = nums1
        }else{
            minNums = nums1
            maxNums = nums2
        }

        minNums.forEach {
            if (duplicateNumbers.contains(it)) {

            }else{
                duplicateNumbers.add(it)
                if (maxNums.contains(it)) {
                    numbers.add(it)
                }
            }
        }
        return  numbers.toIntArray()
    }
    fun standardIntersection(nums1: IntArray, nums2: IntArray): IntArray {
        val set1 = HashSet<Int>()
        val array1Set = HashSet<Int>()

        nums1.forEach {
            array1Set.add(it)
        }

        array1Set.forEach {
            if (nums2.contains(it)){
                set1.add(it)
            }
        }
        return  set1.toIntArray()
    }
}