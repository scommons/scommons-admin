package scommons.admin.client.system

import scommons.admin.client.api.system.SystemData
import scommons.client.ui._
import scommons.react.test.TestSpec
import scommons.react.test.raw.ShallowInstance
import scommons.react.test.util.ShallowRendererUtils

class SystemEditPanelSpec extends TestSpec with ShallowRendererUtils {
  
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

  it should "render component" in {
    //given
    val props = getSystemEditPanelProps()
    val component = <(SystemEditPanel())(^.wrapped := props)()

    //when
    val result = shallowRender(component)

    //then
    assertSystemEditPanel(result, props)
  }

  it should "render component and requestFocus on first input field" in {
    //given
    val props = getSystemEditPanelProps(requestFocus = true)
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

  private def assertSystemEditPanel(result: ShallowInstance, props: SystemEditPanelProps): Unit = {
    val data = props.initialData

    assertNativeComponent(result, <.div()(), { case List(nameComp, passwordComp, titleComp, urlComp) =>
      assertNativeComponent(nameComp, <.div()(), { case List(labelComp, fieldComp) =>
        assertNativeComponent(labelComp, <.label()("Name"))
        assertComponent(fieldComp, TextField) {
          case TextFieldProps(text, _, requestFocus, requestSelect, className, placeholder, _, readOnly) =>
            text shouldBe data.name
            requestFocus shouldBe props.requestFocus
            requestSelect shouldBe props.requestFocus
            className shouldBe Some("input-xlarge")
            placeholder shouldBe None
            readOnly shouldBe props.readOnly
        }
      })
      assertNativeComponent(passwordComp, <.div()(), { case List(labelComp, fieldComp) =>
        assertNativeComponent(labelComp, <.label()("Password"))
        assertComponent(fieldComp, PasswordField) {
          case PasswordFieldProps(password, _, requestFocus, requestSelect, className, placeholder, _, readOnly) =>
            password shouldBe data.password
            requestFocus shouldBe false
            requestSelect shouldBe false
            className shouldBe Some("input-xlarge")
            placeholder shouldBe None
            readOnly shouldBe props.readOnly
        }
      })
      assertNativeComponent(titleComp, <.div()(), { case List(labelComp, fieldComp) =>
        assertNativeComponent(labelComp, <.label()("Title"))
        assertComponent(fieldComp, TextField) {
          case TextFieldProps(text, _, requestFocus, requestSelect, className, placeholder, _, readOnly) =>
            text shouldBe data.title
            requestFocus shouldBe false
            requestSelect shouldBe false
            className shouldBe Some("input-xlarge")
            placeholder shouldBe None
            readOnly shouldBe props.readOnly
        }
      })
      assertNativeComponent(urlComp, <.div()(), { case List(labelComp, fieldComp) =>
        assertNativeComponent(labelComp, <.label()("URL"))
        assertComponent(fieldComp, TextField) {
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
  }
}
