/*
 * DISCLAIMER
 *
 * Copyright 2016 ArangoDB GmbH, Cologne, Germany
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Copyright holder is ArangoDB GmbH, Cologne, Germany
 */

package com.arangodb.jackson.dataformat.velocypack.polymorphic

import com.arangodb.jackson.dataformat.velocypack.VPackMapper
import com.arangodb.velocypack.VPackSlice
import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonTypeInfo
import com.fasterxml.jackson.module.kotlin.KotlinModule
import org.hamcrest.MatcherAssert
import org.hamcrest.Matchers
import org.junit.Test


/**
 * @author Michele Rastelli
 */

class KotlinPolymorphicDeserializationTest {

    @Test
    fun deserializeKotlinContainer() {

        val container = ContainerKt(
                attributes = mapOf(
                        "FirstType" to PolyTypeKt.FirstTypeKt(key = "FirstType", value = "FirstType"),
                        "SecondType" to PolyTypeKt.SecondTypeKt(key = "SecondType", value = "SecondType")
                ),
                text = listOf("a", "b")
        )

        val mapper = VPackMapper().apply {
            // since Jackson 2.17 KotlinModule default constructor is hidden (DeprecationLevel = HIDDEN)
            registerModule(KotlinModule::class.java.getConstructor().newInstance())
        }

        val bytes = mapper.writeValueAsBytes(container)

        val json = VPackSlice(bytes).toString()

        val result = mapper.readValue(bytes, ContainerKt::class.java)
        MatcherAssert.assertThat(result, Matchers.`is`(container))

        println("Json:\t $json")
        println("Original:\t\t $container")
        println("Deserialized:\t\t $result")

    }

}

data class ContainerKt(
        val attributes: Map<String, PolyTypeKt>,
        val text: List<String>
)

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXTERNAL_PROPERTY, property = "type")
@JsonSubTypes(
        JsonSubTypes.Type(name = "FirstTypeKt", value = PolyTypeKt.FirstTypeKt::class),
        JsonSubTypes.Type(name = "SecondTypeKt", value = PolyTypeKt.SecondTypeKt::class)
)
sealed class PolyTypeKt {
    data class FirstTypeKt(
            val key: String,
            val value: String
    ) : PolyTypeKt()

    data class SecondTypeKt(
            val key: String,
            val value: String
    ) : PolyTypeKt()
}
