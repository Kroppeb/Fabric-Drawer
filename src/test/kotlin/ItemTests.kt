import kotlinx.serialization.KSerializer
import kotlinx.serialization.modules.SerialModule
import net.minecraft.Bootstrap
import org.junit.jupiter.api.Test
import utils.*
//import utils.squaredTestCases
import kotlin.test.assertEquals

abstract class ItemTests {
	init {
		if (!initialized) {
			Bootstrap.initialize()
			initialized = true
		}
	}

	@Test
	open fun `Verify all cases are tested`() {
		assertEquals(31, testCases.size)
	}

	@Test
	open fun `Check the squared testcase set`(){
		testMethod(::run, items=testMaps )
	}

	@Test
	open fun `simple object`() {
		test(testCases[0])
	}

	@Test
	open fun `simple nested object`() {
		test(testCases[1])
	}

	@Test
	open fun `simple object with nullable nested object`() {
		test(testCases[2])
	}

	@Test
	open fun `simple object with null`() {
		test(testCases[3])
	}

	@Test
	open fun `simple object with list`() {
		test(testCases[4])
	}

	@Test
	open fun `complex object`() {
		test(testCases[5])
	}

	@Test
	open fun `nested objects with vararg`() {
		test(testCases[6])
	}

	@Test
	open fun `external serializers`() {
		test(testCases[7])
	}

	@Test
	open fun `test LazyCase`() {
		test(testCases[8])
	}

	@Test
	open fun `various zeros`() {
		test(testCases[9])
	}

	@Test
	open fun `various nullable zeros`() {
		test(testCases[10])
	}

	@Test
	open fun `various tags`() {
		test(testCases[11])
	}

	@Test
	open fun `various primitive array tags`() {
		test(testCases[12])
	}

	@Test
	open fun `abstract tags, testing polymorphism`() {
		test(testCases[13])
	}

	@Test
	open fun `custom module`() {
		test(testCases[14])
	}

	@Test
	open fun `listTags`() {
		test(testCases[15])
	}

	@Test
	open fun `compoundTags`() {
		test(testCases[16])
	}

	@Test
	open fun `itemStacks`() {
		test(testCases[17])
	}

	@Test
	open fun `ingredients`() {
		test(testCases[18])
	}

	@Test
	open fun `defaultedList`() {
		test(testCases[19])
	}

	@Test
	open fun `non-nullable lists without nulls`() {
		test(testCases[20])
	}
	@Test
	open fun `nullable lists without nulls`() {
		test(testCases[21])
	}
	@Test
	open fun `nullable list with nulls`() {
		test(testCases[22])
	}

	@Test
	open fun `lists ending with nulls`(){
		test(testCases[23])
	}

	@Test
	open fun `enum to enum map`(){
		test(testCases[24])
	}

	@Test
	open fun `enum to int map`(){
		test(testCases[25])
	}

	@Test
	open fun `list of enums`(){
		test(testCases[26])
	}

	@Test
	open fun `enum to string map`(){
		test(testCases[27])
	}

	@Test
	open fun `string to enum map`(){
		test(testCases[28])
	}

	@Test
	open fun `int to emoji enum map`(){
		test(testCases[29])
	}

	@Test
	open fun `emoji enum to string map`(){
		test(testCases[30])
	}

	private fun test(case: Case<*>) {
		@Suppress("UNCHECKED_CAST")
		if(!testCase(case as Case<Any>, ::run))
			throw Exception("Testcase failed")
	}

	abstract fun run(kSerializer: KSerializer<Any>, any: Any, serialModule: SerialModule): TestResult
}