package com.technogenis.expensior

class MyClass {

    companion object{

        @JvmStatic
        fun main(args: Array<String>){
            println("HELLO WORLD")

            var name = Pair("Rehman",Pair("Hello",2))

            var tripleName = Triple("Rehman","2","4")







            println(name.second.second)
            println(tripleName.third)
            println(doubleReturn())


        }

        fun doubleReturn() : Any{
            return Pair("Hello","2")
        }



    }





}