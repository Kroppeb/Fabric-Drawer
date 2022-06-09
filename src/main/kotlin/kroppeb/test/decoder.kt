package kroppeb.test

import com.mojang.datafixers.util.Pair
import com.mojang.serialization.DataResult
import kotlinx.serialization.*
import kotlinx.serialization.CompositeDecoder.Companion.READ_DONE
import kotlinx.serialization.modules.SerialModule
import kotlinx.serialization.Decoder
import kotlin.streams.toList

class DynamicDecoderBinder<T>(
    val ops: OpsWrapper<T>,
    val input: T,
    val stringify: Boolean = false,
    override val context: SerialModule
) : Decoder {
    override val updateMode: UpdateMode
        get() = TODO("Not yet implemented")

    override fun beginStructure(desc: SerialDescriptor, vararg typeParams: KSerializer<*>): CompositeDecoder =
        when (desc.kind) {
            StructureKind.LIST ->
                if (ops.canEncodeNull() || !desc.getElementDescriptor(0).isNullable)
                    DynamicListDecoder(ops, input, context)
                else
                    DynamicListDecoder(
                        ops, ops.getStream(input).take().toList()
                            .map {
                                ops.getMap(it).take().get("v")
                            }, context
                    )

            StructureKind.CLASS -> DynamicRecordDecoder(ops, input, context)

            StructureKind.MAP -> {
                val keyDesc = desc.getElementDescriptor(0)
                val kind = keyDesc.kind
                when {
                    (kind is PrimitiveKind && kind !is PrimitiveKind.UNIT
                            || kind == UnionKind.ENUM_KIND) && !keyDesc.isNullable ->
                        DynamicSimpleMapDecoder(ops, input, context)
                    else -> if (ops.canEncodeNull()) DynamicListDecoder(ops, input, context)
                    else DynamicSimpleMapDecoder(
                        ops,
                        ops.getStream(input).take()
                            .toList().map {
                                val map = ops.getMap(it).take()
                                Pair(map["key"] as T, map["value"] as T)
                            }.iterator(), context, false
                    )
                }
            }
            is PolymorphicKind -> DynamicSimpleMapDecoder(ops, input, context)
            else -> DynamicSimpleMapDecoder(ops, input, context) //TODO("implement kind : ${desc.kind}")
        }

    //region simple decoders

    override fun decodeBoolean(): Boolean =
        if (stringify) decodeString().toBoolean() else ops.getBooleanValue(input).take()

    override fun decodeByte(): Byte =
        if (stringify) decodeString().toByte() else ops.getNumberValue(input).map { it.toByte() }.take()

    override fun decodeChar(): Char {
        val str = decodeString()
        if (str.length != 1)
            throw DecoderError("Expected a single Char, got \"$str\" instead")
        return str[0]
    }

    override fun decodeDouble(): Double =
        if (stringify) decodeString().toDouble() else ops.getNumberValue(input).map { it.toDouble() }.take()

    override fun decodeEnum(enumDescription: SerialDescriptor): Int =
        if (stringify)
            enumDescription.getElementIndex(decodeString())
        else
            decodeInt()

    override fun decodeFloat(): Float =
        if (stringify) decodeString().toFloat() else ops.getNumberValue(input).map { it.toFloat() }.take()

    override fun decodeInt(): Int =
        if (stringify) decodeString().toInt() else ops.getNumberValue(input).map { it.toInt() }.take()

    override fun decodeLong(): Long =
        if (stringify) decodeString().toLong() else ops.getNumberValue(input).map { it.toLong() }.take()

    override fun decodeNotNullMark(): Boolean = !ops.isEmpty(input)

    override fun decodeNull(): Nothing? = null

    override fun decodeShort(): Short =
        if (stringify) decodeString().toShort() else ops.getNumberValue(input).map { it.toShort() }.take()

    override fun decodeString(): String = ops.getStringValue(input).take()

    override fun decodeUnit() = Unit
    //endregion simple decoders


}

abstract class DynamicCompositeDecoder<T>(val ops: OpsWrapper<T>, override val context: SerialModule) :
    CompositeDecoder {
    final override val updateMode: UpdateMode
        get() = TODO("Not yet implemented")

    protected abstract fun getElement(desc: SerialDescriptor, index: Int): T
    protected abstract fun getNullableElement(desc: SerialDescriptor, index: Int): T?

    override fun decodeBooleanElement(desc: SerialDescriptor, index: Int): Boolean {
        val value = getElement(desc, index)
        return ops.getBooleanValue(value).take()
    }

    override fun decodeByteElement(desc: SerialDescriptor, index: Int): Byte {
        val value = getElement(desc, index)
        return ops.getNumberValue(value).take().toByte()
    }

    override fun decodeCharElement(desc: SerialDescriptor, index: Int): Char {
        val str = decodeStringElement(desc, index)
        if (str.length != 1)
            throw DecoderError("Expected a single Char, got \"$str\" instead")
        return str[0]
    }

    override fun decodeDoubleElement(desc: SerialDescriptor, index: Int): Double {
        val value = getElement(desc, index)
        return ops.getNumberValue(value).take().toDouble()
    }

    override fun decodeFloatElement(desc: SerialDescriptor, index: Int): Float {
        val value = getElement(desc, index)
        return ops.getNumberValue(value).take().toFloat()
    }

    override fun decodeIntElement(desc: SerialDescriptor, index: Int): Int {
        val value = getElement(desc, index)
        return ops.getNumberValue(value).take().toInt()
    }

    override fun decodeLongElement(desc: SerialDescriptor, index: Int): Long {
        val value = getElement(desc, index)
        return ops.getNumberValue(value).take().toLong()
    }

    override fun decodeShortElement(desc: SerialDescriptor, index: Int): Short {
        val value = getElement(desc, index)
        return ops.getNumberValue(value).take().toShort()
    }

    override fun decodeStringElement(desc: SerialDescriptor, index: Int): String {
        val value = getElement(desc, index)
        return ops.getStringValue(value).take()
    }

    override fun decodeUnitElement(desc: SerialDescriptor, index: Int) = Unit

    override fun <T : Any> decodeNullableSerializableElement(
        desc: SerialDescriptor,
        index: Int,
        deserializer: DeserializationStrategy<T?>
    ): T? {
        val value = getNullableElement(desc, index) ?: return null
        if (ops.isEmpty(value))
            return null
        return deserializer.deserialize(DynamicDecoderBinder(ops, value, context = context))
    }

    override fun <A> decodeSerializableElement(
        desc: SerialDescriptor,
        index: Int,
        deserializer: DeserializationStrategy<A>
    ): A {
        val value = getElement(desc, index)
        return deserializer.deserialize(DynamicDecoderBinder(ops, value, context = context))
    }

    override fun <T : Any> updateNullableSerializableElement(
        desc: SerialDescriptor,
        index: Int,
        deserializer: DeserializationStrategy<T?>,
        old: T?
    ): T? {
        TODO("Implement updateNullableSerializableElement")
    }

    override fun <T> updateSerializableElement(
        desc: SerialDescriptor,
        index: Int,
        deserializer: DeserializationStrategy<T>,
        old: T
    ): T {
        TODO("Implement updateSerializableElement")
    }
}

class DynamicRecordDecoder<T>(ops: OpsWrapper<T>, private val input: Map<String, T>, context: SerialModule) :
    DynamicCompositeDecoder<T>(ops, context) {
    constructor(ops: OpsWrapper<T>, input: T, context: SerialModule) : this(
        ops,
        ops.getMap(input).take().entries().toList().associate { ops.getStringValue(it.first).take() to it.second },
        context
    )

    var value: T? = null
    override fun getElement(desc: SerialDescriptor, index: Int): T {
        return value as T
    }

    override fun getNullableElement(desc: SerialDescriptor, index: Int): T? {
        return value
    }

    private var lastIndex = -1
    override tailrec fun decodeElementIndex(desc: SerialDescriptor): Int {
        val index = ++lastIndex
        if (index >= desc.elementsCount)
            return READ_DONE
        val name = desc.getElementName(index)
        if (!ops.canEncodeNull() || name in input) {
            value = input[name]
            return index
        }
        return decodeElementIndex(desc)
    }
}

class DynamicListDecoder<T>(ops: OpsWrapper<T>, private val input: List<T?>, context: SerialModule) :
    DynamicCompositeDecoder<T>(ops, context) {
    constructor(ops: OpsWrapper<T>, input: T, context: SerialModule) : this(
        ops,
        ops.getStream(input).take().toList(),
        context
    )


    override fun getElement(desc: SerialDescriptor, index: Int): T = input[index] as T
    override fun getNullableElement(desc: SerialDescriptor, index: Int): T? = input[index]


    var nextIndex = 0
    override fun decodeElementIndex(desc: SerialDescriptor): Int {
        val next = nextIndex++
        if (next >= input.size)
            return READ_DONE
        return next
    }
}

class DynamicSimpleMapDecoder<T>(ops: OpsWrapper<T>, val input: Iterator<Pair<T, T>>, context: SerialModule, val stringify: Boolean) :
    DynamicCompositeDecoder<T>(ops, context) {
    constructor(ops: OpsWrapper<T>, input: T, context: SerialModule) : this(
        ops,
        ops.getMap(input).take().entries().iterator(),
        context, !ops.compressMaps()
    )

    lateinit var value: Pair<T, T>
    override fun getElement(desc: SerialDescriptor, index: Int): T {
        return if (index % 2 == 0) value.first else value.second
    }

    override fun getNullableElement(desc: SerialDescriptor, index: Int): T? {
        return if (index % 2 == 0) value.first else value.second
    }

    var index = -1 // last read index
    override fun decodeElementIndex(desc: SerialDescriptor): Int {
        if (++index % 2 == 0) {
            if (!input.hasNext())
                return READ_DONE
            value = input.next()
        }
        return index
    }

    override fun <A> decodeSerializableElement(
        desc: SerialDescriptor,
        index: Int,
        deserializer: DeserializationStrategy<A>
    ): A {
        val value = getElement(desc, index)
        return deserializer.deserialize(
            DynamicDecoderBinder(
                ops,
                value,
                stringify && index % 2 == 0,
                context
            )
        )
    }

}


fun <R> DataResult<R>.take(): R = this.getOrThrow(false) {
    throw DecoderError(it)
}

class DecoderError(msg: String) : SerializationException(msg)