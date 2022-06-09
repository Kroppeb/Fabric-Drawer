@file:UseSerializers(
    ForUuid::class,
    ForBlockPos::class,
    ForIdentifier::class,
    ForByteTag::class,
    ForShortTag::class,
    ForIntTag::class,
    ForLongTag::class,
    ForFloatTag::class,
    ForDoubleTag::class,
    ForEndTag::class,
    ForByteArrayTag::class,
    ForIntArrayTag::class,
    ForLongArrayTag::class,
    ForStringTag::class,
    ForListTag::class,
    ForCompoundTag::class,
    ForItemStack::class,
    ForIngredient::class,
    ForVec3d::class,
    ForSoundEvent::class
)

package utils

import drawer.*
import kotlinx.serialization.*
import kotlinx.serialization.UseSerializers
import kotlinx.serialization.internal.nullable
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.serializer
import net.minecraft.client.sound.SoundInstance
import net.minecraft.item.ItemStack
import net.minecraft.item.Items
import net.minecraft.nbt.*
import net.minecraft.recipe.Ingredient
import net.minecraft.sound.SoundCategory
import net.minecraft.sound.SoundEvent
import net.minecraft.sound.SoundEvents
import net.minecraft.util.Identifier
import net.minecraft.util.collection.DefaultedList
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Vec3d
import java.util.*
import kotlin.math.nextDown
import kotlin.math.nextUp

// simple data objects

@Serializable
data class VariousNumbers(
    val int: Int,
    val byte: Byte,
    val short: Short,
    val float: Float,
    val long: Long,
    val double: Double
)

@Serializable
data class VariousNullableNumbers(
    val int: Int?,
    val byte: Byte?,
    val short: Short,
    val float: Float?,
    val long: Long,
    val double: Double
)

@Serializable
data class CityData(
    val id: Int,
    val name: String
)

@Serializable
data class StreetData(
    val id: Int,
    val name: String,
    val city: CityData
)

@Serializable
data class StreetData2(
    val id: Int,
    val name: String,
    val city: CityData?
)

@Serializable
data class CountyData(
    val name: String,
    val cities: List<CityData>
)

@Serializable
data class Tags(
    val byte: ByteTag,
    val short: ShortTag,
    val int: IntTag,
    val long: LongTag,
    val float: FloatTag,
    val double: DoubleTag,
    val string: StringTag,
    val end: EndTag,
    val byteArray: ByteArrayTag,
    val intArray: IntArrayTag,
    val longArray: LongArrayTag
)

@Serializable
data class IntArrayTagWrapper(
    val end: EndTag,
    val byteArray: ByteArrayTag,
    val intArray: IntArrayTag,
    val longArray: LongArrayTag
)


// Shop from Kotlin Koans

@Serializable
data class Shop(val name: String, val customers: List<Customer>)

@Serializable
data class Customer(val name: String, val city: City, val orders: List<Order>) {
    override fun toString() = "$name from ${city.name} with $orders"
}

@Serializable
data class Order(val products: List<Product>, val isDelivered: Boolean) {
    override fun toString() = "$products${if (isDelivered) " delivered" else ""}"
}

@Serializable
data class Product(val name: String, val price: Double) {
    override fun toString() = "'$name' for $price"
}

@Serializable
data class City(val name: String) {
    override fun toString() = name
}


interface PolymorphicMessage

@Serializable
data class IntMessage(val int: Int) : PolymorphicMessage

@Serializable
data class StringMessage(val string: String) : PolymorphicMessage

@Serializable
data class MessageWrapper(val message: PolymorphicMessage, val stringMessage: StringMessage)


// TestShop from Kotlin Koans

//products
val idea = Product("IntelliJ IDEA Ultimate", 199.0)
val reSharper = Product("ReSharper", 149.0)
val dotTrace = Product("DotTrace", 159.0)
val dotMemory = Product("DotTrace", 129.0)
val dotCover = Product("DotCover", 99.0)
val appCode = Product("AppCode", 99.0)
val phpStorm = Product("PhpStorm", 99.0)
val pyCharm = Product("PyCharm", 99.0)
val rubyMine = Product("RubyMine", 99.0)
val webStorm = Product("WebStorm", 49.0)
val teamCity = Product("TeamCity", 299.0)
val youTrack = Product("YouTrack", 500.0)

//customers
val lucas = "Lucas"
val cooper = "Cooper"
val nathan = "Nathan"
val reka = "Reka"
val bajram = "Bajram"
val asuka = "Asuka"

//cities
val Canberra = City("Canberra")
val Vancouver = City("Vancouver")
val Budapest = City("Budapest")
val Ankara = City("Ankara")
val Tokyo = City("Tokyo")

fun customer(name: String, city: City, vararg orders: Order) = Customer(name, city, orders.toList())
fun order(vararg products: Product, isDelivered: Boolean = true) = Order(products.toList(), isDelivered)
fun shop(name: String, vararg customers: Customer) = Shop(name, customers.toList())

val shop = shop(
    "jb test shop",
    customer(
        lucas, Canberra,
        order(reSharper),
        order(reSharper, dotMemory, dotTrace)
    ),
    customer(cooper, Canberra),
    customer(
        nathan, Vancouver,
        order(rubyMine, webStorm)
    ),
    customer(
        reka, Budapest,
        order(idea, isDelivered = false),
        order(idea, isDelivered = false),
        order(idea)
    ),
    customer(
        bajram, Ankara,
        order(reSharper)
    ),
    customer(
        asuka, Tokyo,
        order(idea)
    )
)

// Zoo from library tests by Roman Elizarov

enum class Attitude { POSITIVE, NEUTRAL, NEGATIVE }

@Serializable
data class IntData(val intV: Int)

@Serializable
data class Tree(val name: String, val left: Tree? = null, val right: Tree? = null)

@Serializable
data class Zoo(
    val unit: Unit,
    val boolean: Boolean,
    val byte: Byte,
    val short: Short,
    val int: Int,
    val long: Long,
    val float: Float,
    val double: Double,
    val char: Char,
    val string: String,
    val enum: Attitude,
    val intData: IntData,
    val unitN: Unit?,
    val booleanN: Boolean?,
    val byteN: Byte?,
    val shortN: Short?,
    val intN: Int?,
    val longN: Long?,
    val floatN: Float?,
    val doubleN: Double?,
    val charN: Char?,
    val stringN: String?,
    val enumN: Attitude?,
    val intDataN: IntData?,
    val listInt: List<Int>,
    val listIntN: List<Int?>,
    val listNInt: List<Int>?,
    val listNIntN: List<Int?>?,
    val listListEnumN: List<List<Attitude?>>,
    val listIntData: List<IntData>,
    val listIntDataN: List<IntData?>,
    val tree: Tree,
    val mapStringInt: Map<String, Int>,
    val mapIntStringN: Map<Int, String?>,
    val arrays: ZooWithArrays
)

@Serializable
data class ZooWithArrays(
    val arrByte: Array<Byte>,
    val arrInt: Array<Int>,
    val arrIntN: Array<Int?>,
    val arrIntData: Array<IntData>

) {
    override fun equals(other: Any?) = other is ZooWithArrays &&
            arrByte.contentEquals(other.arrByte) &&
            arrInt.contentEquals(other.arrInt) &&
            arrIntN.contentEquals(other.arrIntN) &&
            arrIntData.contentEquals(other.arrIntData)
}

val zoo = Zoo(
    Unit, true, 10, 20, 30, 40, 50f, 60.0, 'A', "Str0", Attitude.POSITIVE, IntData(70),
    null, null, 11, 21, 31, 41, 51f, 61.0, 'B', "Str1", Attitude.NEUTRAL, null,
    listOf(1, 2, 3),
    listOf(4, 5, null),
    listOf(6, 7, 8),
    listOf(null, 9, 10),
    listOf(listOf(Attitude.NEGATIVE, null)),
    listOf(IntData(1), IntData(2), IntData(3)),
    listOf(IntData(1), null, IntData(3)),
    Tree("root", Tree("left"), Tree("right", Tree("right.left"), Tree("right.right"))),
    mapOf("one" to 1, "two" to 2, "three" to 3),
    mapOf(0 to null, 1 to "first", 2 to "second"),
    ZooWithArrays(
        arrayOf(1, 2, 3),
        arrayOf(100, 200, 300),
        arrayOf(null, -1, -2),
        arrayOf(IntData(1), IntData(2))
    )
)

@Serializable
data class OtherFormats(
    val uuid: UUID,
    val uuidList: List<UUID>,
    val blockPos: BlockPos,
    val blockPosList: List<BlockPos>,
    val id: Identifier,
    val vec3d: Vec3d,
    val soundCategory: SoundCategory,
    val attenuationType: SoundInstance.AttenuationType

)


val otherFormats = OtherFormats(
    UUID(1, 222222222),
    listOf(UUID(2, 3), UUID(4, 5), UUID(11111111111111111, 9)),
    BlockPos(78, 12, 2),
    listOf(BlockPos(4, 5, 6), BlockPos(7, 8, 9), BlockPos(10, 11, 12)),
    Identifier("spatialcrafting", "x2crafter_piece"),
    Vec3d(0.2, -123.0, 2323.3),
    SoundCategory.AMBIENT, SoundInstance.AttenuationType.LINEAR
)

@Serializable
data class OtherLazyFormats(val soundEvent: SoundEvent)

val otherLazyFormats = {
    OtherLazyFormats(SoundEvents.AMBIENT_UNDERWATER_LOOP)
}


val zeroNumbers = VariousNumbers(0, 0, 0, 0f, 0, 0.0)
val nullableZeroNumbers = VariousNullableNumbers(null, 0, 0, 0f, 0, 0.0)

val message = MessageWrapper(
    IntMessage(1),
    StringMessage("Asdf")
)
val messageModule = SerializersModule {
    polymorphic(PolymorphicMessage::class) {
        IntMessage::class with IntMessage.serializer()
        StringMessage::class with StringMessage.serializer()
    }
}

val tags = Tags(
    ByteTag.of(0),
    ShortTag.of(1),
    IntTag.of(2),
    LongTag.of(3),
    FloatTag.of(3.5f),
    DoubleTag.of(4.23),
    StringTag.of("amar"),
    EndTag.INSTANCE,
    ByteArrayTag(listOf(5.toByte(), 6.toByte(), 7.toByte())),
    IntArrayTag(listOf(8, 9, 10)),
    LongArrayTag(listOf(11L, 12L, 13L))
)

val intArrayTagWrapper = IntArrayTagWrapper(
    EndTag.INSTANCE, ByteArrayTag(listOf(5.toByte(), 6.toByte(), 7.toByte())),
    IntArrayTag(listOf(8, 9, 10)), LongArrayTag(listOf(11L, 12L, 13L))
)

@Serializable
data class AbstractTags(
    val tag1: Tag,
    val tag2: Tag,
    val tag3: Tag,
    val tag4: Tag,
    val tag5: Tag,
    val tag6: Tag,
    val tag7: Tag,
    val tag8: Tag,
    val tag9: Tag,
    val tag10: Tag,
    val tag11: Tag,
    val tag12: Tag,
    val tag13: Tag
)


val abstractTags = AbstractTags(
    ByteTag.of(0),
    ShortTag.of(1),
    IntTag.of(2),
    LongTag.of(3),
    FloatTag.of(3.5f),
    DoubleTag.of(4.23),
    StringTag.of("amar"),
    EndTag.INSTANCE,
    ByteArrayTag(listOf(5.toByte(), 6.toByte(), 7.toByte())),
    IntArrayTag(listOf(8, 9, 10)),
    LongArrayTag(listOf(11L, 12L, 13L)),
    ListTag().apply { add(LongTag.of(2L)) },
    CompoundTag().apply { putBoolean("as", false) }
)

@Serializable
data class LessAbstractTags(val tag: Tag)

val lessAbstractTags = LessAbstractTags(IntArrayTag(listOf(8, 9, 10)))


@Serializable
data class ListTags(
    val listTag1: ListTag,
    val listTag2: ListTag,
    val listTag3: ListTag,
    val listTag4: ListTag,
    val listTag5: ListTag
)

//TODO: list tag of compound tag
val listTags = ListTags(ListTag().apply {
    add(IntTag.of(1))
    add(IntTag.of(2))
    add(IntTag.of(3))
}, ListTag().apply {
    add(StringTag.of("asdf"))
    add(StringTag.of("asdf"))
},
    ListTag().apply {
        add(ByteArrayTag(listOf(1.toByte(), 2.toByte(), 3.toByte())))
        add(ByteArrayTag(listOf(2.toByte(), 4.toByte(), 3.toByte())))
        add(ByteArrayTag(listOf((-13).toByte(), 2.toByte(), 4.toByte())))
    },
    ListTag().apply {
        add(ListTag().apply { add(ByteTag.of(1.toByte())) })
        add(ListTag().apply { add(FloatTag.of(0.3f)) })
    },
    ListTag().apply {
        CompoundTag().apply {
            putInt("asdf", 1)
        }
        CompoundTag().apply {
            putString("asdf", "ASdf")
        }
    }

)

@Serializable
data class CompoundTags(val compoundTag1: CompoundTag, val compoundTag2: CompoundTag)

val compoundTags = CompoundTags(
    CompoundTag().apply {
        put("", tags.intArray)
        put("asdfff", tags.double)
        ForUuid.put(UUID(1, 2), this, key = "amar")
    },
    CompoundTag().apply {
        put("heavy shit", listTags.listTag5)
        putDouble("dd", 12.3)
    }
)

@Serializable
data class LessCompoundTags(val compoundTag1: CompoundTag/*, val compoundTag2 : CompoundTag*/)

val lessCompoundTags = LessCompoundTags(
    CompoundTag().apply {
        ForUuid.put(UUID(1, 2), this, key = "amar")
    }

)

@Serializable
data class ItemStacks(val itemStack1: ItemStack, val itemStack2: ItemStack, val itemStack3: ItemStack) {
    override fun equals(other: Any?): Boolean = other is ItemStacks
            && ItemStack.areEqual(this.itemStack1, other.itemStack1)
            && ItemStack.areEqual(this.itemStack2, other.itemStack2)
            && ItemStack.areEqual(this.itemStack3, other.itemStack3)

    override fun hashCode(): Int {
        var result = itemStack1.hashCode()
        result = 31 * result + itemStack2.hashCode()
        result = 31 * result + itemStack3.hashCode()
        return result
    }
}

val itemStacks = {
    ItemStacks(ItemStack(Items.ACACIA_WOOD, 2), ItemStack.EMPTY, ItemStack.fromTag(
        CompoundTag().apply {
            putString("id", "birch_planks")
            putByte("Count", 64)
            put("tag", CompoundTag().apply {
                putString("aaa", "hello")
                putInt("waefwe", 222)
            })

        }
    ))

}

@Serializable
data class Ingredients(val ingredient1: Ingredient, val ingredient2: Ingredient, val ingredient3: Ingredient) {
    override fun equals(other: Any?) = other is Ingredients &&
            ingredient1 actuallyEquals ingredient2 &&
            ingredient2 actuallyEquals ingredient2 &&
            ingredient3 actuallyEquals ingredient3

    override fun hashCode(): Int {
        var result = ingredient1.hashCode()
        result = 31 * result + ingredient2.hashCode()
        result = 31 * result + ingredient3.hashCode()
        return result
    }
}

infix fun Ingredient.actuallyEquals(other: Ingredient): Boolean {
    return other.matchingStacksClient.zip(other.matchingStacksClient)
        .all { (stack1, stack2) -> ItemStack.areEqual(stack1, stack2) }
}

val ingredients = {
    Ingredients(
        Ingredient.EMPTY,
        Ingredient.ofStacks(itemStacks().itemStack1, itemStacks().itemStack2, itemStacks().itemStack3),
        Ingredient.ofItems(Items.CARROT, Items.IRON_CHESTPLATE, Items.SPAWNER)
    )
}


@Serializable
data class DefaultedLists(
    @Serializable(with = ForDefaultedList::class) val itemStackList: DefaultedList<ItemStack>,
    @Serializable(with = ForDefaultedList::class) val ingredientList: DefaultedList<Ingredient>,
    @Serializable(with = ForDefaultedList::class) val intList: DefaultedList<Int>
) {
    override fun equals(other: Any?): Boolean {
        return other is DefaultedLists &&
                this.itemStackList.zip(other.itemStackList)
                    .all { (thisStack, otherStack) -> ItemStack.areEqual(thisStack, otherStack) }
                && this.ingredientList.zip(other.ingredientList)
            .all { (thisIngredient, otherIngredient) -> thisIngredient actuallyEquals otherIngredient }
                && this.intList == other.intList
    }

    override fun hashCode(): Int {
        var result = itemStackList.hashCode()
        result = 31 * result + ingredientList.hashCode()
        result = 31 * result + intList.hashCode()
        return result
    }
}

val defaultedLists = {
    DefaultedLists(
        DefaultedList.copyOf(
            ItemStack.EMPTY,
            itemStacks().itemStack3,
            itemStacks().itemStack1,
            ItemStack.EMPTY,
            ItemStack(Items.ITEM_FRAME)
        ),
        DefaultedList.ofSize(10, Ingredient.EMPTY),
        DefaultedList.copyOf(0, 1, 2, 3, 4, 5, 6, 7, 8)
    )
}

@Serializable
data class NullableLists(
    val ints: List<Int?>,
    val shorts: List<Short?>,
    val bytes: List<Byte?>,
    val longs: List<Long?>,
    val floats: List<Float?>,
    val doubles: List<Double?>
)

@Serializable
data class NonNullableLists(
    val ints: List<Int>,
    val shorts: List<Short>,
    val bytes: List<Byte>,
    val longs: List<Long>,
    val floats: List<Float>,
    val doubles: List<Double>
)

val nonnullableListNoNull = NonNullableLists(
    listOf(-5, 0, 1, -1, Int.MIN_VALUE, Int.MAX_VALUE, 2, -2),
    listOf(-5, 0, 1, -1, Short.MIN_VALUE, Short.MAX_VALUE, 2, -2),
    listOf(-5, 0, 1, -1, Byte.MIN_VALUE, Byte.MAX_VALUE, 2, -2),
    listOf(-5, 0, 1, -1, Long.MIN_VALUE, Long.MAX_VALUE, 2, -2),
    listOf(
        -5.0f,
        0.0f,
        // -0.0f, // nbt doesn't handle this correctly
        1.0f,
        -1.0f,
        0.5f,
        -0.5f,
        2.0f,
        -2.0f,
        Float.MAX_VALUE,
        -Float.MAX_VALUE,
        Float.MIN_VALUE,
        -Float.MIN_VALUE,
        Float.MAX_VALUE.nextDown(),
        (-Float.MAX_VALUE).nextUp(),
        Float.MIN_VALUE.nextUp(),
        (-Float.MIN_VALUE).nextDown(),
        Float.NEGATIVE_INFINITY,
        Float.POSITIVE_INFINITY,
        Float.NaN
    ),
    listOf(
        -5.0,
        0.0,
        // -0.0, // nbt doesn't handle this correctly
        1.0,
        -1.0,
        0.5,
        -0.5,
        2.0,
        -2.0,
        Double.MAX_VALUE,
        -Double.MAX_VALUE,
        Double.MIN_VALUE,
        -Double.MIN_VALUE,
        Double.MAX_VALUE.nextDown(),
        (-Double.MAX_VALUE).nextUp(),
        Double.MIN_VALUE.nextUp(),
        (-Double.MIN_VALUE).nextDown(),
        Double.NEGATIVE_INFINITY,
        Double.POSITIVE_INFINITY,
        Double.NaN
    )
)

val nullableListNoNull = NullableLists(
    listOf(-5, 0, 1, -1, Int.MIN_VALUE, Int.MAX_VALUE, 2, -2),
    listOf(-5, 0, 1, -1, Short.MIN_VALUE, Short.MAX_VALUE, 2, -2),
    listOf(-5, 0, 1, -1, Byte.MIN_VALUE, Byte.MAX_VALUE, 2, -2),
    listOf(-5, 0, 1, -1, Long.MIN_VALUE, Long.MAX_VALUE, 2, -2),
    listOf(
        -5.0f,
        0.0f,
        // -0.0f, // nbt doesn't handle this correctly
        1.0f,
        -1.0f,
        0.5f,
        -0.5f,
        2.0f,
        -2.0f,
        Float.MAX_VALUE,
        -Float.MAX_VALUE,
        Float.MIN_VALUE,
        -Float.MIN_VALUE,
        Float.MAX_VALUE.nextDown(),
        (-Float.MAX_VALUE).nextUp(),
        Float.MIN_VALUE.nextUp(),
        (-Float.MIN_VALUE).nextDown(),
        Float.NEGATIVE_INFINITY,
        Float.POSITIVE_INFINITY,
        Float.NaN
    ),
    listOf(
        -5.0,
        0.0,
        // -0.0, // nbt doesn't handle this correctly
        1.0,
        -1.0,
        0.5,
        -0.5,
        2.0,
        -2.0,
        Double.MAX_VALUE,
        -Double.MAX_VALUE,
        Double.MIN_VALUE,
        -Double.MIN_VALUE,
        Double.MAX_VALUE.nextDown(),
        (-Double.MAX_VALUE).nextUp(),
        Double.MIN_VALUE.nextUp(),
        (-Double.MIN_VALUE).nextDown(),
        Double.NEGATIVE_INFINITY,
        Double.POSITIVE_INFINITY,
        Double.NaN
    )
)

val nullableListWithNulls = NullableLists(
    listOf(-5, 0, 1, null, -1, Int.MIN_VALUE, Int.MAX_VALUE, 2, null, -2),
    listOf(-5, 0, 1, -1, null, Short.MIN_VALUE, null, Short.MAX_VALUE, 2, -2),
    listOf(-5, 0, null, 1, -1, Byte.MIN_VALUE, Byte.MAX_VALUE, 2, -2, null),
    listOf(-5, null, 0, 1, -1, null, Long.MIN_VALUE, Long.MAX_VALUE, 2, -2),
    listOf(
        -5.0f,
        0.0f,
        // -0.0f, // nbt doesn't handle this correctly
        null,
        1.0f,
        -1.0f,
        0.5f,
        -0.5f,
        null,
        2.0f,
        -2.0f
    ),
    listOf(
        -5.0,
        0.0,
        null,
        // -0.0, // nbt doesn't handle this correctly
        1.0,
        -1.0,
        0.5,
        -0.5,
        2.0,
        null,
        -2.0,
        null
    )
)

val listsEndingWithNull = NullableLists(
    listOf(0, null, null, null, null, null, null, null),
    listOf<Short?>(0, null, null, null, null, null, null, null, null),
    listOf<Byte?>(0, null, null, null, null, null, null, null, null, null),
    listOf<Long?>(0, null, null, null, null, null, null, null, null, null, null),
    listOf<Float?>(0.0f, null, null, null, null, null, null, null, null, null, null),
    listOf<Double?>(0.0, null, null, null, null, null, null, null, null, null, null, null)
)

@Serializable
enum class Sign {
    POSITIVE, ZERO, NEGATIVE
}


@Serializable
enum class Game {
    ROCK, PAPER, SCISSORS
}


val beats = mutableMapOf(
    Game.ROCK to Game.SCISSORS,
    Game.SCISSORS to Game.PAPER,
    Game.PAPER to Game.ROCK
)

val signToValue = Sign.values().toList().associateWith { 1 - it.ordinal }

val byteToSign = (0..255).map {
    when (it) {
        0 -> Sign.ZERO
        in 1..127 -> Sign.POSITIVE
        else -> Sign.NEGATIVE
    }
}

val gameToString =
    Game.values().toList().associateWith { it.toString().toLowerCase() } // TODO remove the list step in 1.4
val stringToGame = gameToString.entries.associate { it.value to it.key }


@Suppress("EnumEntryName")
@Serializable
enum class Emotion {
    `😀`, `😁`, `😂`, `🤣`, `😃`, `😄`, `😅`, `😆`, `😉`, `😊`, `😋`, `😎`, `😍`, `😘`, `🥰`, `😗`, `😙`, `😚`, `☺️`, `🙂`, `🤗`, `🤩`, `🤔`, `🤨`, `😐`, `😑`, `😶`, `🙄`, `😏`, `😣`, `😥`, `😮`, `🤐`, `😯`, `😪`, `😫`, `😴`, `😌`, `😛`, `😜`, `😝`, `🤤`, `😒`, `😓`, `😔`, `😕`, `🙃`, `🤑`, `😲`, `☹️`, `🙁`, `😖`, `😞`, `😟`, `😤`, `😢`
}

val idToEmotion = Emotion.values().associateBy { (it.name.hashCode() * 8455969985695 % 3659885).toInt() }
val emotionToString = idToEmotion.toList().associate { (id, value) -> value to "$id: ${value.name}" }

@Serializable
sealed class SealedClass {
	@Serializable
	object A : SealedClass()

	@Serializable
	data class B(val t: Int) : SealedClass()

	@Serializable
	data class C(val s: String) : SealedClass()

	@Serializable
	data class D(val u: Unit) : SealedClass()

	@Serializable
	data class E(val p: Int? = null) : SealedClass()
}

@Serializable
sealed class TypedSealedClass<out L, out R> {
	@Serializable
	object Fail : TypedSealedClass<String, Int>() // Nothing doesn't seem to work?

	@Serializable
	data class Error<out L>(val error: L) : TypedSealedClass<L, Int>()

	@Serializable
	data class Success<out R>(val value: R) : TypedSealedClass<String, R>()

	@Serializable
	data class Warning<out L, out R>(val warning: L, val value: R) : TypedSealedClass<L, R>()

	@Serializable
	data class Partial<out L, out R>(val error: L, val partial: R) : TypedSealedClass<L, R>()
}

private infix fun <E> List<E>.with(serializer: KSerializer<E>) = this to serializer

val testSealedData =
	listOf(
		SealedClass.A,
		SealedClass.B(0),
		SealedClass.C("hello"),
		SealedClass.D(Unit),
		SealedClass.E(null)
	) with SealedClass.serializer()

val testTypedSealedData = listOf(
	TypedSealedClass.Fail,
	TypedSealedClass.Error("Server caught fire"),
	TypedSealedClass.Success(123654),
	TypedSealedClass.Warning("Server is very busy", 123654),
	TypedSealedClass.Partial("Server is too busy", 123000)
) with TypedSealedClass.serializer(String.serializer(), Int.serializer())

val testEnumData = Emotion.values().toList().take(5) with Emotion.serializer()

val testByteData = listOf<Byte>(0, -5, 9, 1, Byte.MAX_VALUE) with Byte.serializer()
val testShortData = listOf<Short>(0, -5, 9, 1, Short.MAX_VALUE) with Short.serializer()
val testIntData = listOf<Int>(0, -5, 9, 1, Int.MAX_VALUE) with Int.serializer()
val testLongData = listOf<Long>(0, -5, 9, 1, Long.MAX_VALUE) with Long.serializer()
val testFloatData = listOf<Float>(0.0f, -5.0f, 9.0f, 1.0f, Float.MIN_VALUE) with Float.serializer()
val testDoubleData = listOf<Double>(0.0, -5.0, 9.0, 1.0, Double.MIN_VALUE) with Double.serializer()

val testNullableByteData = listOf<Byte?>(0, -5, null, 1, Byte.MAX_VALUE) with Byte.serializer().nullable
val testNullableShortData = listOf<Short?>(0, -5, null, 1, Short.MAX_VALUE) with Short.serializer().nullable
val testNullableIntData = listOf<Int?>(0, -5, null, 1, Int.MAX_VALUE) with Int.serializer().nullable
val testNullableLongData = listOf<Long?>(0, -5, null, 1, Long.MAX_VALUE) with Long.serializer().nullable
val testNullableFloatData = listOf<Float?>(0.0f, null, 9.0f, 1.0f, Float.MIN_VALUE) with Float.serializer().nullable
val testNullableDoubleData = listOf<Double?>(0.0, null, 9.0, 1.0, Double.MIN_VALUE) with Double.serializer().nullable

val testStringData = listOf("Hello", "there", "how are you?", "one two three", "") with String.serializer()
val testNullableStringData = listOf("Hello", null, "how are you?", "one two three", "") with String.serializer().nullable

val testIntLists = (0..4).map{((it * 5) until it * it + it*5).toList()} with Int.serializer().list
