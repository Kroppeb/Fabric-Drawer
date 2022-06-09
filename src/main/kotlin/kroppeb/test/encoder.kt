package kroppeb.test

import com.mojang.serialization.ListBuilder
import com.mojang.serialization.RecordBuilder
import kotlinx.serialization.*
import kotlinx.serialization.modules.SerialModule


class DynamicEncoderBinder<T>(
	val ops: OpsWrapper<T>, prefix: T = ops.empty(), val stringify: Boolean = false,
	override val context: SerialModule
) : Encoder {
	private var pref: T = prefix
	var value: T
		get() = pref
		set(value) {
			pref = ops.mergeToPrimitive(pref, value).take()

		}

	override fun beginStructure(desc: SerialDescriptor, vararg typeParams: KSerializer<*>): CompositeEncoder =
		when (desc.kind) {
			StructureKind.LIST ->
				if (ops.canEncodeNull() || !desc.getElementDescriptor(0).isNullable)
					DynamicListEncoder()
				else
					DynamicNullableListEncoder()
			StructureKind.CLASS -> DynamicRecordEncoder()
			StructureKind.MAP -> {
				val keyDesc = desc.getElementDescriptor(0)
				val kind = keyDesc.kind
				when {
					(kind is PrimitiveKind && kind !is PrimitiveKind.UNIT
							|| kind == UnionKind.ENUM_KIND) && !keyDesc.isNullable ->
						DynamicSimpleMapEncoder()
					else -> if (ops.canEncodeNull()) DynamicListEncoder() else DynamicComplexMapEncoder()
				}
			}
			is PolymorphicKind -> DynamicSimpleMapEncoder()
			else -> DynamicSimpleMapEncoder() //TODO("implement kind : ${desc.kind}")
		}


	override fun encodeBoolean(value: Boolean) {
		this.value =
			if (stringify)
				ops.createString(value.toString())
			else
				ops.createBoolean(value)
	}

	override fun encodeByte(value: Byte) {
		this.value =
			if (stringify)
				ops.createString(value.toString())
			else
				ops.createByte(value)
	}

	override fun encodeChar(value: Char) {
		encodeString(value.toString())
	}

	override fun encodeDouble(value: Double) {
		this.value =
			if (stringify)
				ops.createString(value.toString())
			else
				ops.createDouble(value)
	}

	override fun encodeEnum(enumDescription: SerialDescriptor, ordinal: Int) {
		if (stringify)
			encodeString(enumDescription.getElementName(ordinal))
		else
			encodeInt(ordinal)
	}

	override fun encodeFloat(value: Float) {
		this.value =
			if (stringify)
				ops.createString(value.toString())
			else
				ops.createFloat(value)
	}

	override fun encodeInt(value: Int) {
		this.value =
			if (stringify)
				ops.createString(value.toString())
			else
				ops.createInt(value)
	}

	override fun encodeLong(value: Long) {
		this.value =
			if (stringify)
				ops.createString(value.toString())
			else
				ops.createLong(value)
	}

	override fun encodeNotNullMark() {

	}

	override fun encodeNull() {
		this.value = ops.empty()
	}

	override fun encodeShort(value: Short) {
		this.value =
			if (stringify)
				ops.createString(value.toString())
			else
				ops.createShort(value)
	}

	override fun encodeString(value: String) {
		this.value = ops.createString(value)
	}

	override fun encodeUnit() {
		this.value =
			if (stringify)
				throw DecoderError("Can't stringyfy Unit")
			else
				ops.emptyMap()
	}

	abstract inner class DynamicCompositeEncoder : CompositeEncoder {
		override val context: SerialModule
			get() = this@DynamicEncoderBinder.context


		protected abstract fun addElement(desc: SerialDescriptor, index: Int, value: T)


		override fun encodeBooleanElement(desc: SerialDescriptor, index: Int, value: Boolean) {
			addElement(desc, index, ops.createBoolean(value))
		}

		override fun encodeByteElement(desc: SerialDescriptor, index: Int, value: Byte) {
			addElement(desc, index, ops.createByte(value))
		}

		override fun encodeCharElement(desc: SerialDescriptor, index: Int, value: Char) {
			encodeStringElement(desc, index, value.toString())
		}

		override fun encodeDoubleElement(desc: SerialDescriptor, index: Int, value: Double) {
			addElement(desc, index, ops.createDouble(value))
		}

		override fun encodeFloatElement(desc: SerialDescriptor, index: Int, value: Float) {
			addElement(desc, index, ops.createFloat(value))
		}

		override fun encodeIntElement(desc: SerialDescriptor, index: Int, value: Int) {
			addElement(desc, index, ops.createInt(value))
		}

		override fun encodeLongElement(desc: SerialDescriptor, index: Int, value: Long) {
			addElement(desc, index, ops.createLong(value))
		}

		override fun encodeShortElement(desc: SerialDescriptor, index: Int, value: Short) {
			addElement(desc, index, ops.createShort(value))
		}

		override fun encodeStringElement(desc: SerialDescriptor, index: Int, value: String) {
			addElement(desc, index, ops.createString(value))
		}

		override fun encodeUnitElement(desc: SerialDescriptor, index: Int) {
			addElement(desc, index, ops.emptyMap())
		}

		override fun encodeNonSerializableElement(desc: SerialDescriptor, index: Int, value: Any) {
			TODO("Implement encodeNonSerializableElement")
		}

		override fun <T : Any> encodeNullableSerializableElement(
			desc: SerialDescriptor,
			index: Int,
			serializer: SerializationStrategy<T>,
			value: T?
		) {
			if (value != null)
				encodeSerializableElement(desc, index, serializer, value)
			else
				addElement(desc, index, ops.empty())
		}

		override fun <T> encodeSerializableElement(
			desc: SerialDescriptor,
			index: Int,
			serializer: SerializationStrategy<T>,
			value: T
		) {
			addElement(desc, index, DynamicEncoderBinder(ops, context = context).also {
				serializer.serialize(it, value)
			}.value)
		}
	}

	inner class DynamicRecordEncoder : DynamicCompositeEncoder() {
		private val builder: RecordBuilder<T> = ops.mapBuilder()

		override fun shouldEncodeElementDefault(desc: SerialDescriptor, index: Int): Boolean = !ops.canEncodeNull()

		override fun endStructure(desc: SerialDescriptor) {
			pref = builder.build(pref).take()
		}

		override fun addElement(desc: SerialDescriptor, index: Int, value: T) {
			if (ops.canEncodeNull() || !ops.isEmpty(value))
				builder.add(desc.getElementName(index), value)
		}
	}

	inner class DynamicNullableListEncoder : DynamicCompositeEncoder() {
		private val builder: ListBuilder<T> = ops.listBuilder()

		override fun endStructure(desc: SerialDescriptor) {
			pref = builder.build(pref).take()
		}

		override fun addElement(desc: SerialDescriptor, index: Int, value: T) {
			if (ops.isEmpty(value))
				builder.add(ops.emptyMap())
			else
				builder.add(
					ops.createMap(
						mapOf(
							ops.createString("v") to value
						)
					)
				)
		}
	}

	inner class DynamicSimpleMapEncoder : DynamicCompositeEncoder() {
		private val builder: RecordBuilder<T> = ops.mapBuilder()

		override fun shouldEncodeElementDefault(desc: SerialDescriptor, index: Int): Boolean = true

		override fun endStructure(desc: SerialDescriptor) {
			pref = builder.build(pref).take()
		}

		var key: T? = null
		override fun addElement(desc: SerialDescriptor, index: Int, value: T) {
			if (index % 2 == 0)
				key = value
			else
				builder.add(key, value)
		}

		override fun <T> encodeSerializableElement(
			desc: SerialDescriptor,
			index: Int,
			serializer: SerializationStrategy<T>,
			value: T
		) {
			addElement(
				desc,
				index,
				DynamicEncoderBinder(ops, stringify = !ops.compressMaps() && index % 2 == 0, context = context).also {
					serializer.serialize(it, value)
				}.value
			)
		}

	}

	inner class DynamicComplexMapEncoder : DynamicCompositeEncoder() {
		private val builder: ListBuilder<T> = ops.listBuilder()

		override fun shouldEncodeElementDefault(desc: SerialDescriptor, index: Int): Boolean = true

		override fun endStructure(desc: SerialDescriptor) {
			pref = builder.build(pref).take()
		}

		val KEY = ops.createString("key")
		val VALUE = ops.createString("value")
		var key: T? = null
		override fun addElement(desc: SerialDescriptor, index: Int, value: T) {

			if (index % 2 == 0)
				key = value
			else {
				val entry = mutableMapOf<T, T>()
				if(ops.canEncodeNull() || !ops.isEmpty(key))
					entry[KEY] = key as T
				if(ops.canEncodeNull() || !ops.isEmpty(value))
					entry[VALUE] = value

				builder.add(
					ops.createMap(entry)
				)
			}
		}


	}

	inner class DynamicListEncoder : DynamicCompositeEncoder() {
		private val builder: ListBuilder<T> = ops.listBuilder()

		override fun shouldEncodeElementDefault(desc: SerialDescriptor, index: Int): Boolean = true

		override fun endStructure(desc: SerialDescriptor) {
			pref = builder.build(pref).take()
		}

		override fun addElement(desc: SerialDescriptor, index: Int, value: T) {
			if (ops.canEncodeNull() || !ops.isEmpty(value))
				builder.add(value)
		}
	}
}
