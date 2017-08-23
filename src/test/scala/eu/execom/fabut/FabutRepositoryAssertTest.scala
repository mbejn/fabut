package eu.execom.fabut

import eu.execom.fabut.model._
import eu.execom.fabut.property.CopyAssert
import eu.execom.fabut.report.FabutReportBuilder
import org.junit.Assert.{assertFalse, assertTrue}
import org.junit.Test

import scala.collection.mutable.ListBuffer
import scala.reflect.runtime.universe.typeOf

class FabutRepositoryAssertTest extends AbstractFabutRepositoryAssertTest {

  val TEST = "test"
  val TEST_ID = 105
  val PROPERTY = "_property"
  val IS_PROPERTY = true
  val DOT = "."
  val ENTITY_TWO_PROPERTY = "_subProperty._property"

  @Test
  def testAssertDbStateTrue(): Unit = {
    //    setup
    val beforeList1 = ListBuffer(new EntityTierOneType(TEST, 1), new EntityTierOneType(TEST, 2))
    setEntityTierOneTypes(beforeList1)

    val beforeList2 = ListBuffer(
      new EntityTierTwoType(PROPERTY + PROPERTY + PROPERTY, new EntityTierOneType(TEST, 1), 4))
    setEntityTierTwoTypes(beforeList2)

    val report = new FabutReportBuilder
    fabutAssert.takeSnapshot()(report)

    val afterList1 = ListBuffer(new EntityTierOneType(TEST, 1), new EntityTierOneType(TEST, 2))
    setEntityTierOneTypes(afterList1)
    val afterList2 = ListBuffer(new EntityTierTwoType(PROPERTY + PROPERTY + PROPERTY, new EntityTierOneType(TEST, 1), 4))
    setEntityTierTwoTypes(afterList2)

    //    method
    val assertResult = fabutAssert.assertDbSnapshot(new FabutReportBuilder)

    //    assert
    assertTrue(assertResult)
  }

  @Test
  def testAssertDbStateFalse(): Unit = {
    //    setup
    val beforeList1 = ListBuffer(new EntityTierOneType(TEST, 1), new EntityTierOneType(TEST, 2))
    setEntityTierOneTypes(beforeList1)

    val beforeList2 = ListBuffer(
      new EntityTierTwoType(PROPERTY + PROPERTY + PROPERTY, new EntityTierOneType("greska", 7), 4))
    setEntityTierTwoTypes(beforeList2)

    val report = new FabutReportBuilder
    fabutAssert.takeSnapshot()(report)

    val afterList1 = ListBuffer(new EntityTierOneType(TEST, 1), new EntityTierOneType(TEST, 2))
    setEntityTierOneTypes(afterList1)
    val afterList2 = ListBuffer(new EntityTierTwoType(PROPERTY + PROPERTY + PROPERTY, new EntityTierOneType(TEST + TEST, 6), 4))
    setEntityTierTwoTypes(afterList2)

    //    method
    val assertResult = fabutAssert.assertDbSnapshot(new FabutReportBuilder)

    //    assert
    assertFalse(assertResult)

  }

  @Test
  def testAssertEntityAsDeletedEntity(): Unit = {
    //    setup
    val list1: ListBuffer[EntityTierOneType] = ListBuffer()
    list1 += new EntityTierOneType(TEST, 1)
    list1 += new EntityTierOneType(TEST, 2)
    setEntityTierOneTypes(list1)

    val report = new FabutReportBuilder
    val actual = new EntityTierOneType(TEST, 1)
    fabutAssert.takeSnapshot()(report)

    val list2: ListBuffer[EntityTierOneType] = ListBuffer()
    list2 += new EntityTierOneType(TEST, 2)
    setEntityTierOneTypes(list2)

    //    method
    val assertEntityAsDeleted = fabutAssert.assertEntityAsDeleted(new FabutReportBuilder, actual)
    val assertDbState = fabutAssert.assertDbSnapshot(new FabutReportBuilder)

    //    assert
    assertTrue(assertEntityAsDeleted)
    assertTrue(assertDbState)
  }

  @Test
  def testIgnoreEntityEntity(): Unit = {
    //    setup
    val list1: ListBuffer[EntityTierOneType] = ListBuffer()
    list1 += new EntityTierOneType(TEST, 1)
    list1 += new EntityTierOneType(TEST, 2)
    setEntityTierOneTypes(list1)

    val report = new FabutReportBuilder
    val actual = new EntityTierOneType(TEST, 1)
    fabutAssert.takeSnapshot()(report)

    val list2: ListBuffer[EntityTierOneType] = ListBuffer()
    list2 += new EntityTierOneType(TEST, 2)
    setEntityTierOneTypes(list2)

    //    method
    val ignoreEntity = fabutAssert.ignoreEntity(actual)(report)
    val assertDbState = fabutAssert.assertDbSnapshot(report)

    //    assert
    assertTrue(ignoreEntity)
    assertTrue(assertDbState)
  }

  @Test
  def testAfterAssertEntityParentEntityNotProperty(): Unit = {
    //    setup
    val list1: ListBuffer[EntityTierOneType] = ListBuffer()
    list1 += new EntityTierOneType(TEST, 1)
    list1 += new EntityTierOneType(TEST, 2)
    setEntityTierOneTypes(list1)

    val report = new FabutReportBuilder
    val actual = new EntityTierOneType(TEST, 1)
    fabutAssert.takeSnapshot()(report)

    val list2: ListBuffer[EntityTierOneType] = ListBuffer()
    list2 += new EntityTierOneType(TEST, 2)
    setEntityTierOneTypes(list2)

    //    method
    val afterAssertEntity = fabutAssert.afterAssertEntity(report, actual, !IS_PROPERTY)
    val assertDbState = fabutAssert.assertDbSnapshot(report)

    //    assert
    assertTrue(afterAssertEntity)
    assertTrue(assertDbState)
  }

  @Test(expected = classOf[AssertionError])
  def testAfterAssertEntityIsProperty(): Unit = {
    //    setup
    val list1: ListBuffer[EntityTierOneType] = ListBuffer()
    list1 += new EntityTierOneType(TEST, 1)
    list1 += new EntityTierOneType(TEST, 2)
    setEntityTierOneTypes(list1)

    val report = new FabutReportBuilder
    val actual = new EntityTierOneType(TEST, 1)
    fabutAssert.takeSnapshot()(report)

    val list2: ListBuffer[EntityTierOneType] = ListBuffer()
    list2 += new EntityTierOneType(TEST, 2)
    setEntityTierOneTypes(list2)

    //    method
    fabutAssert.afterAssertEntity(report, actual, IS_PROPERTY)
    val assertResult = fabutAssert.assertDbSnapshot(report)

    //    assert
    assertTrue(assertResult)
  }

  @Test
  def testAfterAssertEntityNotEntity(): Unit = {
    //    setup
    val list1: ListBuffer[EntityTierOneType] = ListBuffer()
    list1 += new EntityTierOneType(TEST, 1)
    list1 += new EntityTierOneType(TEST, 2)
    setEntityTierOneTypes(list1)

    val report = new FabutReportBuilder
    val actual = FakeEntity
    fabutAssert.takeSnapshot()(report)

    val list2: ListBuffer[EntityTierOneType] = ListBuffer()
    list2 += new EntityTierOneType(TEST, 2)
    setEntityTierOneTypes(list2)

    //    method
    val assertResult = fabutAssert.afterAssertEntity(report, actual, !IS_PROPERTY)

    //    assert
    assertFalse(assertResult)
  }

  @Test
  def testAfterAssertEntityWithoutID(): Unit = {
    //    setup
    val list1: ListBuffer[EntityTierOneType] = ListBuffer()
    list1 += new EntityTierOneType(TEST, 1)
    list1 += new EntityTierOneType(TEST, 2)
    setEntityTierOneTypes(list1)

    val report = new FabutReportBuilder
    val actual = new EntityTierOneType(TEST, 1)
    fabutAssert.takeSnapshot()(report)

    //    method
    val afterAssertEntity = fabutAssert.afterAssertEntity(report, actual, !IS_PROPERTY)
    val assertDbState = fabutAssert.assertDbSnapshot(report)

    //    assert
    assertTrue(afterAssertEntity)
    assertTrue(assertDbState)
  }

  @Test
  def testMarkAssertedNotTypeSupportedTypeFalse(): Unit = {
    val report = new FabutReportBuilder
    //    method
    fabutAssert.takeSnapshot()(report)
    val assertValue = fabutAssert.markAsAsserted(new FabutReportBuilder, new UnknownEntityType(4), Some(typeOf[UnknownEntityType]))
    val assertDbState = fabutAssert.assertDbSnapshot(new FabutReportBuilder)

    //    assert
    assertFalse(assertValue)
    assertTrue(assertDbState)
  }

  @Test
  def testMarkAssertedCopyAssertNull(): Unit = {
    val list: ListBuffer[EntityTierTwoType] = ListBuffer()
    setEntityTierTwoTypes(list)
    val report = new FabutReportBuilder

    //    method
    fabutAssert.takeSnapshot()(report)
    list += new EntityTierTwoType(TEST, new EntityTierOneType(TEST + TEST, 10), 1)
    val assertValue = fabutAssert.markAsAsserted(new FabutReportBuilder, new EntityTierTwoType(TEST, new EntityTierOneType(TEST + TEST, 10), 1), Some(typeOf[EntityTierTwoType]))
    val assertDbState = fabutAssert.assertDbSnapshot(new FabutReportBuilder)

    //    assert
    assertTrue(assertValue)
    assertTrue(assertDbState)
  }

  @Test
  def testMarkAssertedCopyAssertNotNull(): Unit = {

    val entity: EntityTierTwoType = new EntityTierThreeType(TEST, new EntityTierOneType(TEST + TEST, 10), 1)
    val list: ListBuffer[EntityTierTwoType] = ListBuffer()
    list += entity
    setEntityTierTwoTypes(list)
    val report = new FabutReportBuilder

    //    method
    fabutAssert.takeSnapshot()(report)
    entity.property_=("new" + TEST)
    val assertValue = fabutAssert.markAsAsserted(new FabutReportBuilder, new EntityTierTwoType(TEST, new EntityTierOneType(TEST + TEST, 10), 1), Some(typeOf[EntityTierTwoType]))
    val assertDbState = fabutAssert.assertDbSnapshot(new FabutReportBuilder)

    //    assert
    assertTrue(assertValue)
    assertTrue(assertDbState)
  }

  @Test
  def testCheckNotExistingInAfterDbStateTrue(): Unit = {
    //    setup
    val beforeIds: Set[Any] = Set(1, 2, 3)
    val afterIds: Set[Any] = Set(1, 3)
    val report = new FabutReportBuilder

    val copyAssert2 = CopyAssert(new EntityTierOneType)
    copyAssert2.asserted_=(true)
    val beforeEntities: Map[Any, CopyAssert] = Map(1 -> CopyAssert(new EntityTierOneType), 2 -> copyAssert2, 3 -> CopyAssert(new EntityTierOneType))
    //    method
    val assertResult = fabutAssert.checkNotExistingInAfterDbState(beforeIds, afterIds, beforeEntities)(report)

    //    assert
    assertTrue(assertResult)
  }

  @Test
  def testCheckNotExistingInAfterDbStateFalse(): Unit = {
    //    setup
    val beforeIds: Set[Any] = Set(1, 2, 3)
    val afterIds: Set[Any] = Set(1, 3)

    val beforeEntities: Map[Any, CopyAssert] = Map(1 -> CopyAssert(new EntityTierOneType), 2 -> CopyAssert(new EntityTierOneType), 3 -> CopyAssert(new EntityTierOneType))
    //    method
    val assertResult = fabutAssert.checkNotExistingInAfterDbState(beforeIds, afterIds, beforeEntities)(new FabutReportBuilder)

    //    assert
    assertFalse(assertResult)
  }

  @Test
  def testCheckAddedToAfterDbStateFalse(): Unit = {
    //    setup
    val beforeIds: Set[Any] = Set(1, 2)
    val afterIds: Set[Any] = Set(1, 3)

    val afterEntities: Map[Any, Any] = Map(1 -> new EntityTierOneType, 3 -> new EntityTierOneType)
    //    method
    val assertResult = fabutAssert.checkNewToAfterDbState(beforeIds, afterIds, afterEntities)(new FabutReportBuilder)

    //    assert
    assertFalse(assertResult)
  }

  @Test
  def testCheckAddedToAfterDbStateTrue(): Unit = {
    //    setup
    val beforeIds: Set[Any] = Set(1, 3)
    val afterIds: Set[Any] = Set(1, 3)

    val afterEntities: Map[Any, Any] = Map(1 -> new EntityTierOneType, 3 -> new EntityTierOneType)
    //    method
    val assertResult = fabutAssert.checkNewToAfterDbState(beforeIds, afterIds, afterEntities)(new FabutReportBuilder)

    //    assert
    assertTrue(assertResult)
  }

  @Test
  def testAssertDbSnapshotWithAfterStateTrue(): Unit = {
    //    setup
    val beforeIds: Set[Any] = Set(1, 3)
    val afterIds: Set[Any] = Set(1, 3)

    val afterEntities: Map[Any, Object] = Map(1 -> new EntityTierOneType, 3 -> new EntityTierOneType(TEST, 3))

    val copyAssert1 = CopyAssert(new EntityTierOneType(TEST, 1))
    copyAssert1.asserted_=(true)

    val copyAssert2 = CopyAssert(new EntityTierOneType)
    copyAssert2.asserted_=(true)

    val copyAssert3 = CopyAssert(new EntityTierOneType(TEST, 3))

    val beforeEntities: Map[Any, CopyAssert] = Map(1 -> copyAssert1, 2 -> copyAssert2, 3 -> copyAssert3)
    //    method
    val assertResult = fabutAssert.assertDbSnapshotWithAfterState(beforeIds, afterIds, beforeEntities, afterEntities)(new FabutReportBuilder)

    //    assert
    assertTrue(assertResult)
  }

  @Test
  def testAssertDbSnapshotWithAfterStateFalse(): Unit = {
    //    setup
    val beforeIds: Set[Any] = Set(1, 3)
    val afterIds: Set[Any] = Set(1, 3)

    val afterEntities: Map[Any, Object] = Map(1 -> new EntityTierOneType, 3 -> new EntityTierOneType(TEST + TEST, 3))

    val copyAssert1 = CopyAssert(new EntityTierOneType(TEST, 1))
    copyAssert1.asserted_=(true)

    val copyAssert2 = CopyAssert(new EntityTierOneType)
    copyAssert2.asserted_=(true)

    val copyAssert3 = CopyAssert(new EntityTierOneType(TEST, 3))

    val beforeEntities: Map[Any, CopyAssert] = Map(1 -> copyAssert1, 2 -> copyAssert2, 3 -> copyAssert3)

    //    method
    val assertResult = fabutAssert.assertDbSnapshotWithAfterState(beforeIds, afterIds, beforeEntities, afterEntities)(new FabutReportBuilder)

    //    assert
    assertFalse(assertResult)
  }

  @Test
  def testAssertEntityWithSnapshotTrue(): Unit = {
    //    setup
    val list: ListBuffer[EntityTierOneType] = ListBuffer()
    list += new EntityTierOneType(TEST, 1)
    setEntityTierOneTypes(list)

    //    method
    val entity = new EntityTierOneType(TEST + TEST, 1)
    val property = value(EntityTierOneType.PROPERTY, TEST + TEST)
    val properties = createExpectedPropertiesMap(property)
    val report = new FabutReportBuilder

    fabutAssert.takeSnapshot()(report)
    val assertEntityWithSnapshot = fabutAssert.assertEntityWithSnapshot(new FabutReportBuilder, entity, properties)
    val assertDbSnapshot = fabutAssert.assertDbSnapshot(new FabutReportBuilder)

    //    assert
    assertTrue(assertEntityWithSnapshot)
    assertTrue(assertDbSnapshot)
  }

  @Test
  def testAssertEntityWithSnapshotFalse(): Unit = {
    //    setup
    val list: ListBuffer[EntityTierOneType] = ListBuffer()
    setEntityTierOneTypes(list)
    val report = new FabutReportBuilder

    //    method
    val entity = new EntityTierOneType(TEST + TEST, 1)
    val property = value(EntityTierOneType.PROPERTY, TEST + TEST)
    val properties = createExpectedPropertiesMap(property)

    fabutAssert.takeSnapshot()(report)
    val assertEntityWithSnapshot = fabutAssert.assertEntityWithSnapshot(new FabutReportBuilder, entity, properties)
    val assertDbSnapshot = fabutAssert.assertDbSnapshot(new FabutReportBuilder)

    //    assert
    assertFalse(assertEntityWithSnapshot)
    assertTrue(assertDbSnapshot)
  }
}
