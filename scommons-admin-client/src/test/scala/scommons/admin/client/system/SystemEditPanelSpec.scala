package scommons.admin.client.system

import io.github.shogowada.scalajs.reactjs.VirtualDOM._
import scommons.admin.client.api.system.SystemData
import scommons.client.test.TestSpec
import scommons.client.test.raw.ShallowRenderer.ComponentInstance
import scommons.client.ui.{PasswordField, PasswordFieldProps, TextField, TextFieldProps}

class SystemEditPanelSpec extends TestSpec {
  
  it should "call onChange, onEnter when in name field" in {
    //given
    val onChange = mockFunction[SystemData, Unit]
    val onEnter = mockFunction[Unit]
    val props = getSystemEditPanelProps(onChange = onChange, onEnter = onEnter)
    val comp = shallowRender(<(SystemEditPanel())(^.wrapped := props)())
    val fieldProps = findProps(comp, TextField).head
    val value = "updated"
    val data = props.initialData.copy(
      name = value
    )
    
    //then
    onChange.expects(data)
    onEnter.expects()
    
    //when
    fieldProps.onChange(value)
    fieldProps.onEnter()
  }

  it should "call onChange, onEnter when in password field" in {
    //given
    val onChange = mockFunction[SystemData, Unit]
    val onEnter = mockFunction[Unit]
    val props = getSystemEditPanelProps(onChange = onChange, onEnter = onEnter)
    val comp = shallowRender(<(SystemEditPanel())(^.wrapped := props)())
    val fieldProps = findComponentProps(comp, PasswordField)
    val password = "updated"
    val data = props.initialData.copy(
      password = password
    )
    
    //then
    onChange.expects(data)
    onEnter.expects()
    
    //when
    fieldProps.onChange(password)
    fieldProps.onEnter()
  }

  it should "call onChange, onEnter when in title field" in {
    //given
    val onChange = mockFunction[SystemData, Unit]
    val onEnter = mockFunction[Unit]
    val props = getSystemEditPanelProps(onChange = onChange, onEnter = onEnter)
    val comp = shallowRender(<(SystemEditPanel())(^.wrapped := props)())
    val fieldProps = findProps(comp, TextField)(1)
    val value = "updated"
    val data = props.initialData.copy(
      title = value
    )

    //then
    onChange.expects(data)
    onEnter.expects()

    //when
    fieldProps.onChange(value)
    fieldProps.onEnter()
  }

  it should "call onChange, onEnter when in URL field" in {
    //given
    val onChange = mockFunction[SystemData, Unit]
    val onEnter = mockFunction[Unit]
    val props = getSystemEditPanelProps(onChange = onChange, onEnter = onEnter)
    val comp = shallowRender(<(SystemEditPanel())(^.wrapped := props)())
    val fieldProps = findProps(comp, TextField)(2)
    val value = "updated"
    val data = props.initialData.copy(
      url = value
    )

    //then
    onChange.expects(data)
    onEnter.expects()

    //when
    fieldProps.onChange(value)
    fieldProps.onEnter()
  }

  it should "render the component" in {
    //given
    val props = getSystemEditPanelProps()
    val component = <(SystemEditPanel())(^.wrapped := props)()

    //when
    val result = shallowRender(component)

    //then
    assertSystemEditPanel(result, props)
  }

  private def getSystemEditPanelProps(readOnly: Boolean = true,
                                      initialData: SystemData = SystemData(
                                        id = Some(11),
                                        name = "test name",
                                        password = "test password",
                                        title = "test title",
                                        url = "http://test.com",
                                        parentId = 1
                                      ),
                                      requestFocus: Boolean = false,
                                      onChange: SystemData => Unit = _ => (),
                                      onEnter: () => Unit = () => ()): SystemEditPanelProps = {

    SystemEditPanelProps(
      readOnly = readOnly,
      initialData = initialData,
      requestFocus = requestFocus,
      onChange = onChange,
      onEnter = onEnter
    )
  }

  private def assertSystemEditPanel(result: ComponentInstance, props: SystemEditPanelProps): Unit = {
    val data = props.initialData

    assertDOMComponent(result, <.div()(), { case List(nameComp, passwordComp, titleComp, urlComp) =>
      assertDOMComponent(nameComp, <.div()(), { case List(labelComp, fieldComp) =>
        assertDOMComponent(labelComp, <.label()("Name"))
        assertComponent(fieldComp, TextField(), { fieldProps: TextFieldProps =>
          inside(fieldProps) {
            case TextFieldProps(text, _, requestFocus, requestSelect, className, placeholder, _, readOnly) =>
              text shouldBe data.name
              requestFocus shouldBe props.requestFocus
              requestSelect shouldBe props.requestFocus
              className shouldBe Some("input-xlarge")
              placeholder shouldBe None
              readOnly shouldBe props.readOnly
          }
        })
      })
      assertDOMComponent(passwordComp, <.div()(), { case List(labelComp, fieldComp) =>
        assertDOMComponent(labelComp, <.label()("Password"))
        assertComponent(fieldComp, PasswordField(), { fieldProps: PasswordFieldProps =>
          inside(fieldProps) {
            case PasswordFieldProps(password, _, requestFocus, requestSelect, className, placeholder, _, readOnly) =>
              password shouldBe data.password
              requestFocus shouldBe false
              requestSelect shouldBe false
              className shouldBe Some("input-xlarge")
              placeholder shouldBe None
              readOnly shouldBe props.readOnly
          }
        })
      })
      assertDOMComponent(titleComp, <.div()(), { case List(labelComp, fieldComp) =>
        assertDOMComponent(labelComp, <.label()("Title"))
        assertComponent(fieldComp, TextField(), { fieldProps: TextFieldProps =>
          inside(fieldProps) {
            case TextFieldProps(text, _, requestFocus, requestSelect, className, placeholder, _, readOnly) =>
              text shouldBe data.title
              requestFocus shouldBe false
              requestSelect shouldBe false
              className shouldBe Some("input-xlarge")
              placeholder shouldBe None
              readOnly shouldBe props.readOnly
          }
        })
      })
      assertDOMComponent(urlComp, <.div()(), { case List(labelComp, fieldComp) =>
        assertDOMComponent(labelComp, <.label()("URL"))
        assertComponent(fieldComp, TextField(), { fieldProps: TextFieldProps =>
          inside(fieldProps) {
            case TextFieldProps(text, _, requestFocus, requestSelect, className, placeholder, _, readOnly) =>
              text shouldBe data.url
              requestFocus shouldBe false
              requestSelect shouldBe false
              className shouldBe Some("input-xlarge")
              placeholder shouldBe None
              readOnly shouldBe props.readOnly
          }
        })
      })
    })
  }
}
