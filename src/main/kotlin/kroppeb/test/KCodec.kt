package kroppeb.test

import com.mojang.datafixers.util.Pair
import com.mojang.serialization.Codec
import com.mojang.serialization.DataResult
import com.mojang.serialization.DynamicOps
import kotlinx.serialization.KSerializer
import kotlinx.serialization.modules.EmptyModule
import kotlinx.serialization.modules.SerialModule

class KCodec<A>(val serializer: KSerializer<A>, val context: SerialModule, val catchError: Boolean = true) : Codec<A> {
    override fun <T> encode(input: A, ops: DynamicOps<T>, prefix: T): DataResult<T> =
        try {
            val encoder = DynamicEncoderBinder(OpsWrapper(ops), prefix, context = context)
            serializer.serialize(encoder, input)
            DataResult.success(encoder.value)
        } catch (e: DecoderError) {
            if (!catchError)
                throw e
            else
                DataResult.error(e.message)
        }

    fun <T> encode(input: A, ops: DynamicOps<T>): DataResult<T> = encode(input, ops, ops.empty())

    override fun <T> decode(ops: DynamicOps<T>, input: T): DataResult<Pair<A, T>> = try {
        DataResult.success(Pair.of(serializer.deserialize(DynamicDecoderBinder(OpsWrapper(ops), input, context = context)), input))
    } catch (e: DecoderError) {
        if (!catchError)
            throw e
        else
            DataResult.error(e.message)
    }
}

fun <T> KSerializer<T>.asCodec(context: SerialModule = EmptyModule) = KCodec(this, context)
fun <T> KSerializer<T>.asDebugCodec(context: SerialModule = EmptyModule) = KCodec(this, context, false)