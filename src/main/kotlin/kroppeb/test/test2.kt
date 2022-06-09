package kroppeb.test

import com.mojang.serialization.Codec
import com.mojang.serialization.JsonOps
import com.mojang.serialization.codecs.RecordCodecBuilder
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import net.minecraft.class_5308
import net.minecraft.class_5309
import net.minecraft.class_5310

@Serializable
data class virtual_5309(
    val height: Int,
    val sampling: virtual_5308,
    @SerialName("top_slide")
    val topSlide: virtual_5310,
    @SerialName("bottom_slide")
    val bottomSlide: virtual_5310,
    @SerialName("size_horizontal")
    val sizeHorizontal: Int,
    @SerialName("size_vertical")
    val sizeVertical: Int,
    @SerialName("density_factor")
    val densityFactor: Double,
    @SerialName("density_offset")
    val densityOffset: Double,
    @SerialName("simplex_surface_noise")
    val simplexSurfaceNoise: Boolean,
    @SerialName("random_density_offset")
    val randomDensityOffset: Boolean = false,
    @SerialName("island_noise_override")
    val islandNoiseOverride: Boolean = false,
    val amplified: Boolean = false
) {
    companion object {
        fun of(value: class_5309) = virtual_5309(
            value.method_28581(),
            virtual_5308.of(value.method_28583()),
            virtual_5310.of(value.method_28584()),
            virtual_5310.of(value.method_28585()),
            value.method_28586(),
            value.method_28587(),
            value.method_28588(),
            value.method_28589(),
            value.method_28590(),
            value.method_28591(),
            value.method_28592(),
            value.method_28593()
        )
    }
}


@Serializable
data class virtual_5308(
    val xz_scale: Double,
    val y_scale: Double,
    val xz_factor: Double,
    val y_factor: Double
) {
    companion object {
        fun of(input: class_5308) = virtual_5308(
            input.method_28576(),
            input.method_28578(),
            input.method_28579(),
            input.method_28580()
        )
    }
}

@Serializable
data class virtual_5310(
    val target: Int,
    val size: Int,
    val offset: Int
) {
    companion object {
        fun of(input: class_5310) = virtual_5310(
            input.method_28594(),
            input.method_28596(),
            input.method_28597()
        )
    }
}


fun main() {
    val input = class_5309(
        64, class_5308(0.0, 1.0, 3.0, 2.0), class_5310(10, 20, 30),
        class_5310(40, 50, 60), 15, 16, 17.1, 18.1, true, true, true, false
    )
    val ref = virtual_5309.of(input)
    println(ref)
    println()
    val res = class_5309.field_24804.encodeStart(JsonOps.INSTANCE, input).take()
    println(res)
    println()
    val parsed = virtual_5309.serializer().asCodec().decode(JsonOps.INSTANCE, res).take().first
    println(parsed)
    println(parsed == ref)


}