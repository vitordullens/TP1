import GoLBase.RuleGuide

import scala.io.Source
import scalafx.Includes._
import scalafx.application.JFXApp
import scalafx.collections.ObservableBuffer
import scalafx.event.ActionEvent
import scalafx.geometry.{HorizontalDirection, Insets, Pos}
import scalafx.scene.control.Alert.AlertType
import scalafx.scene.{Node, Scene}
import scalafx.scene.control._
import scalafx.scene.layout.{BorderPane, GridPane}
import scalafx.scene.shape.{Circle, Line, Rectangle}
import scalafx.scene.text.{Font, Text}



object CustomRuleCreator extends JFXApp {
  stage = new JFXApp.PrimaryStage {
    title = "Custom Rule Definition"

    scene = new Scene(500, 500) {
      val grid = new GridPane

      stylesheets = List(getClass.getResource("choiceBox.css").toExternalForm)

      var gridRowIdx = 0

      grid.addRow(gridRowIdx, getRuleRow)
      grid.autosize


      val newRule = new Button("New Rule")
      newRule.onAction = (ae : ActionEvent) => {

        if(gridRowIdx < 5) {
          this.grid.addRow(gridRowIdx, getRuleRow)
          this.gridRowIdx += 1
        }
        else{

          new Alert(AlertType.Error) {
            initOwner(stage)
            title = "Rule definition"
            headerText = "Rule Definition Maximum number of Lines Reached"
            contentText = "You've reached the maximum lines available for a rule"
          }.showAndWait()
        }

      }

      val done = new Button("Done")

      val rootPane = new BorderPane
      rootPane.center = grid
      rootPane.bottom = newRule
      rootPane.right = done
      grid.margin = Insets(1, 1, 1, 1)
      content = rootPane
    }

  }


  private def getRules(fileName: String = "rules.txt"): List[RuleGuide] = {
    var rules: List[RuleGuide] = List()
    for (rule <- Source.fromResource(fileName).getLines()) {
      val strategy = Class.forName(rule)
      rules = strategy.newInstance.asInstanceOf[RuleGuide] :: rules
    }
    rules = rules.reverse
    rules
  }

  private def getText(v : String) : Text= {
    val text = new Text(v)
    text.alignmentInParent = Pos.Center
    text
  }

  private def getRuleRow : ToolBar = {

    // definition of what goes in the RuleRow
    val cellState = ObservableBuffer("alive", "dead")
    val modifier = ObservableBuffer("exactly", "more than", "less than")
    val numbers = ObservableBuffer("1", "2", "3", "4", "5", "6", "7", "8")

    val initial = new ChoiceBox[String]
    initial.items = cellState
    initial.getSelectionModel.selectFirst

    val specifier = new ChoiceBox[String]
    specifier.items = modifier
    specifier.getSelectionModel.selectFirst

    val neighbours = new ChoiceBox[String]
    neighbours.items = numbers
    neighbours.getSelectionModel.selectFirst

    val finale = new ChoiceBox[String]
    finale.items = cellState
    finale.getSelectionModel.selectFirst



    // Actually creating the RuleRow
    val ruleRow = new ToolBar
    ruleRow.items = List(getText("A cell"), initial, getText("with"), specifier, neighbours,getText("neighbours. Result:"), finale)
    ruleRow.autosize

    // returning the RuleRow
    ruleRow
  }
}