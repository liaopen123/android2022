package com.hhz.apple_watch_luncher.lib

import org.jbox2d.collision.shapes.PolygonShape
import org.jbox2d.common.Vec2
import org.jbox2d.dynamics.BodyDef
import org.jbox2d.dynamics.BodyType
import org.jbox2d.dynamics.FixtureDef
import org.jbox2d.dynamics.World

class JBoxTest {

    val world: World by lazy {
        World(Vec2(0f, 10f))
    }

    fun updateBound(width:Int, height:Int){
        //创建刚体
        val body = BodyDef().apply {
            type = BodyType.DYNAMIC //
            position.set(-1f, 0f) //确定刚体的位置 在左侧-1,0
        }

        //描述
        FixtureDef().apply {
            //创建多边形
            val polygonShape = PolygonShape().apply {
                setAsBox(1f, height.toFloat())
            }
            shape =  polygonShape
            density = 100f//设置密度
            friction=0.1f //摩擦系数
            restitution = 10f //补偿系数
        }

        world.apply {
            createBody(body) //创建刚体
            createf

        }

    }
}