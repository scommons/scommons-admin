package scommons.admin.client.system

import io.github.shogowada.scalajs.reactjs.VirtualDOM._
import scommons.admin.client.api.system.SystemData
import scommons.client.test.TestSpec
import scommons.client.test.raw.ShallowRenderer.ComponentInstance
import scommons.client.ui.popup.{Modal, ModalProps}
import scommons.client.ui.{ButtonImagesCss, Buttons}

class SystemEditPopupSpec extends TestSpec {

  it should "call onCancel function when cancel command" in {
    //given
    val onCancel = mockFunction[Unit]
    val props = getSystemEditPopupProps("Test message", onCancel = onCancel)
    val component = shallowRender(<(SystemEditPopup())(^.wrapped := props)())
    val modalProps = findComponentProps(component, Modal)

    //then
    onCancel.expects()

    //when
    modalProps.actions.onCommand(_ => ())(Buttons.CANCEL.command)
  }

  it should "call onSave function when SAVE command" in {
    //given
    val onSave = mockFunction[SystemData, Unit]
    val props = getSystemEditPopupProps(onSave = onSave)
    val component = shallowRender(<(SystemEditPopup())(^.wrapped := props)())
    val modalProps = findComponentProps(component, Modal)

    //then
    onSave.expects(props.initialData)

    //when
    modalProps.actions.onCommand(_ => ())(Buttons.SAVE.command)
  }

  it should "call onSave function with the new data when onEnter" in {
    //given
    val onSave = mockFunction[SystemData, Unit]
    val props = getSystemEditPopupProps(onSave = onSave)
    val component = shallowRender(<(SystemEditPopup())(^.wrapped := props)())
    val editProps = findComponentProps(component, SystemEditPanel)
    val newData = props.initialData.copy(name = "updated")
    editProps.onChange(newData)

    //then
    onSave.expects(newData)

    //when
    editProps.onEnter()
  }

  it should "enable SAVE command when all the fields are filled" in {
    //given
    val props = getSystemEditPopupProps()
    val renderer = createRenderer()
    renderer.render(<(SystemEditPopup())(^.wrapped := props)())
    val comp = renderer.getRenderOutput()
    val prevEditProps = findComponentProps(comp, SystemEditPanel)
    val newData = props.initialData.copy(name = "updated")

    //when
    prevEditProps.onChange(newData)

    //then
    val updatedComp = renderer.getRenderOutput()
    val editProps = findComponentProps(updatedComp, SystemEditPanel)
    editProps.initialData shouldBe newData

    val modalProps = findComponentProps(updatedComp, Modal)
    modalProps.actions.enabledCommands shouldBe Set(Buttons.SAVE.command, Buttons.CANCEL.command)
  }

  it should "disable SAVE command when one of the fields is emtpy" in {
    //given
    val props = getSystemEditPopupProps()
    val renderer = createRenderer()
    renderer.render(<(SystemEditPopup())(^.wrapped := props)())
    val comp = renderer.getRenderOutput()
    val prevEditProps = findComponentProps(comp, SystemEditPanel)
    val newData = props.initialData.copy(name = "")

    //when
    prevEditProps.onChange(newData)

    //then
    val updatedComp = renderer.getRenderOutput()
    val editProps = findComponentProps(updatedComp, SystemEditPanel)
    editProps.initialData shouldBe newData

    val modalProps = findComponentProps(updatedComp, Modal)
    modalProps.actions.enabledCommands shouldBe Set(Buttons.CANCEL.command)
  }

  it should "render the component" in {
    //given
    val props = getSystemEditPopupProps()
    val component = <(SystemEditPopup())(^.wrapped := props)()

    //when
    val result = shallowRender(component)

    //then
    assertSystemEditPopup(result, props)
  }

  it should "set requestFocus when onOpen" in {
    //given
    val props = getSystemEditPopupProps("Test message")
    val renderer = createRenderer()
    renderer.render(<(SystemEditPopup())(^.wrapped := props)())
    val comp = renderer.getRenderOutput()
    val modalProps = findComponentProps(comp, Modal)
    val editProps = findComponentProps(comp, SystemEditPanel)
    editProps.requestFocus shouldBe false

    //when
    modalProps.onOpen()

    //then
    val updatedComp = renderer.getRenderOutput()
    val updatedTextProps = findComponentProps(updatedComp, SystemEditPanel)
    updatedTextProps.requestFocus shouldBe true
  }

  it should "reset requestFocus and data when componentWillReceiveProps" in {
    //given
    val prevProps = getSystemEditPopupProps()
    val renderer = createRenderer()
    renderer.render(<(SystemEditPopup())(^.wrapped := prevProps)())
    val comp = renderer.getRenderOutput()
    val editProps = findComponentProps(comp, SystemEditPanel)
    editProps.initialData shouldBe prevProps.initialData
    editProps.requestFocus shouldBe false
    val modalProps = findComponentProps(comp, Modal)
    modalProps.actions.enabledCommands shouldBe Set(Buttons.SAVE.command, Buttons.CANCEL.command)
    modalProps.onOpen()
    val compV2 = renderer.getRenderOutput()
    val textPropsV2 = findComponentProps(compV2, SystemEditPanel)
    textPropsV2.requestFocus shouldBe true
    val props = prevProps.copy(initialData = prevProps.initialData.copy(name = ""))

    //when
    renderer.render(<(SystemEditPopup())(^.wrapped := props)())

    //then
    val compV3 = renderer.getRenderOutput()
    val modalPropsV3 = findComponentProps(compV3, Modal)
    modalPropsV3.actions.enabledCommands shouldBe Set(Buttons.CANCEL.command)

    val textPropsV3 = findComponentProps(compV3, SystemEditPanel)
    textPropsV3.initialData shouldBe props.initialData
    textPropsV3.requestFocus shouldBe false
  }

  private def getSystemEditPopupProps(title: String = "Test Title",
                                      onSave: SystemData => Unit = _ => (),
                                      onCancel: () => Unit = () => (),
                                      initialData: SystemData = SystemData(
                                        id = Some(11),
                                        name = "test name",
                                        password = "test password",
                                        title = "test title",
                                        url = "http://test.com",
                                        parentId = 1
                                      ),
                                      show: Boolean = true): SystemEditPopupProps = SystemEditPopupProps(
    show = show,
    title = title,
    onSave = onSave,
    onCancel = onCancel,
    initialData = initialData
  )

  private def assertSystemEditPopup(result: ComponentInstance, props: SystemEditPopupProps): Unit = {
    val data = props.initialData
    val actionCommands =
      if (data.name.trim.nonEmpty
        && data.password.nonEmpty
        && data.title.trim.nonEmpty
        && data.url.trim.nonEmpty) {
        Set(Buttons.SAVE.command, Buttons.CANCEL.command)
      }
      else Set(Buttons.CANCEL.command)

    assertComponent(result, Modal(), { modalProps: ModalProps =>
      inside(modalProps) { case ModalProps(show, header, buttons, actions, _, onClose, closable, _) =>
        show shouldBe props.show
        header shouldBe Some(props.title)
        buttons shouldBe List(Buttons.SAVE.copy(
          image = ButtonImagesCss.dbSave,
          disabledImage = ButtonImagesCss.dbSaveDisabled,
          primary = true
        ), Buttons.CANCEL)
        actions.enabledCommands shouldBe actionCommands
        onClose shouldBe props.onCancel
        closable shouldBe true
      }
    }, { case List(editPanel) =>
      assertComponent(editPanel, SystemEditPanel(), { panelProps: SystemEditPanelProps =>
        inside(panelProps) {
          case SystemEditPanelProps(readOnly, initialData, requestFocus, _, _) =>
            readOnly shouldBe false
            initialData shouldBe props.initialData
            requestFocus shouldBe false
        }
      })
    })
  }
}
