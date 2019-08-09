package scommons.admin.client.system

import scommons.admin.client.api.system.SystemData
import scommons.react._
import scommons.react.test.TestSpec
import scommons.react.test.util.ShallowRendererUtils

class SystemEditPopupSpec extends TestSpec with ShallowRendererUtils {

  it should "enable save button if all required fields are filled" in {
    //given
    val props = getSystemEditPopupProps()
    val data = props.initialData

    //when & then
    props.isSaveEnabled(data) shouldBe true
    props.isSaveEnabled(data.copy(name = " ")) shouldBe false
    props.isSaveEnabled(data.copy(password = "")) shouldBe false
    props.isSaveEnabled(data.copy(password = " ")) shouldBe true
    props.isSaveEnabled(data.copy(title = " ")) shouldBe false
    props.isSaveEnabled(data.copy(url = " ")) shouldBe false
  }

  it should "render edit panel inside popup" in {
    //given
    val onChange = mockFunction[SystemData, Unit]
    val onSave = mockFunction[Unit]
    val props = getSystemEditPopupProps()
    val wrapper = new FunctionComponent[Unit] {
      protected def render(compProps: Props): ReactElement = {
        props.render(props.initialData, requestFocus = true, onChange, onSave)
      }
    }
    val component = <(wrapper())()()

    //when
    val result = shallowRender(component)

    //then
    assertComponent(result, SystemEditPanel) {
      case SystemEditPanelProps(readOnly, initialData, requestFocus, pOnChange, pOnEnter) =>
        readOnly shouldBe false
        initialData shouldBe props.initialData
        requestFocus shouldBe true
        pOnChange shouldBe onChange
        pOnEnter shouldBe onSave
    }
  }

  private def getSystemEditPopupProps(title: String = "test title",
                                      initialData: SystemData = SystemData(
                                        id = Some(11),
                                        name = "test name",
                                        password = "test password",
                                        title = "test title",
                                        url = "http://test.com",
                                        parentId = 1
                                      ),
                                      onSave: SystemData => Unit = _ => (),
                                      onCancel: () => Unit = () => ()): SystemEditPopupProps = {

    SystemEditPopupProps(
      title = title,
      initialData = initialData,
      onSave = onSave,
      onCancel = onCancel
    )
  }
}
