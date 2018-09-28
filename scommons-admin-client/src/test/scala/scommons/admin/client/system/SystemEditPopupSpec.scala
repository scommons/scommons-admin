package scommons.admin.client.system

import io.github.shogowada.scalajs.reactjs.React
import scommons.admin.client.api.system.SystemData
import scommons.client.test.TestSpec

class SystemEditPopupSpec extends TestSpec {

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
    val wrapper = React.createClass[Unit, Unit] { _ =>
      props.render(props.initialData, requestFocus = true, onChange, onSave)
    }
    val component = <(wrapper)()()

    //when
    val result = shallowRender(component)

    //then
    assertComponent(result, SystemEditPanel(), { panelProps: SystemEditPanelProps =>
      inside(panelProps) {
        case SystemEditPanelProps(readOnly, initialData, requestFocus, pOnChange, pOnEnter) =>
          readOnly shouldBe false
          initialData shouldBe props.initialData
          requestFocus shouldBe true
          pOnChange shouldBe onChange
          pOnEnter shouldBe onSave
      }
    })
  }

  private def getSystemEditPopupProps(show: Boolean = true,
                                      title: String = "test title",
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
      show = show,
      title = title,
      initialData = initialData,
      onSave = onSave,
      onCancel = onCancel
    )
  }
}
