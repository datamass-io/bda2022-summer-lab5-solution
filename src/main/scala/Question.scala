import java.util
import com.fasterxml.jackson.annotation._

import scala.collection.mutable

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder(Array("id", "category", "question", "answer", "round", "show_number"))
class Question {
  @JsonProperty("id") private var id:String = ""
  @JsonProperty("category") private var category:String  = ""
  @JsonProperty("question") private var question:String  = ""
  @JsonProperty("answer") private var answer:String  = ""
  @JsonProperty("round") private var round:String  = ""
  @JsonProperty("show_number") private var showNumber:String  = ""
  @JsonIgnore final private val additionalProperties = new util.HashMap[String, AnyRef]

  @JsonProperty("id") def getId: String = id

  @JsonProperty("id") def setId(id: String): Unit = {
    this.id = id
  }

  @JsonProperty("category") def getCategory: String = category

  @JsonProperty("category") def setCategory(category: String): Unit = {
    this.category = category
  }

  @JsonProperty("question") def getQuestion: String = question

  @JsonProperty("question") def setQuestion(question: String): Unit = {
    this.question = question
  }

  @JsonProperty("answer") def getAnswer: String = answer

  @JsonProperty("answer") def setAnswer(answer: String): Unit = {
    this.answer = answer
  }

  @JsonProperty("round") def getRound: String = round

  @JsonProperty("round") def setRound(round: String): Unit = {
    this.round = round
  }

  @JsonProperty("show_number") def getShowNumber: String = showNumber

  @JsonProperty("show_number") def setShowNumber(showNumber: String): Unit = {
    this.showNumber = showNumber
  }

  @JsonAnyGetter def getAdditionalProperties: util.Map[String, AnyRef] = this.additionalProperties

  @JsonAnySetter def setAdditionalProperty(name: String, value: AnyRef): Unit = {
    this.additionalProperties.put(name, value)
  }

  override def toString: String = {
    val sb = new mutable.StringBuilder
    sb.append(classOf[Question].getName).append('@').append(Integer.toHexString(System.identityHashCode(this))).append('{')
    sb.append("\n")
    sb.append("\t")
    sb.append("id")
    sb.append('=')
    sb.append(if (this.id == null) "<null>"
    else this.id)
    sb.append(',')
    sb.append('\n')
    sb.append("\t")
    sb.append("category")
    sb.append('=')
    sb.append(if (this.category == null) "<null>"
    else this.category)
    sb.append(',')
    sb.append('\n')
    sb.append("\t")
    sb.append("question")
    sb.append('=')
    sb.append(if (this.question == null) "<null>"
    else this.question)
    sb.append(',')
    sb.append('\n')
    sb.append("\t")
    sb.append("answer")
    sb.append('=')
    sb.append(if (this.answer == null) "<null>"
    else this.answer)
    sb.append(',')
    sb.append('\n')
    sb.append("\t")
    sb.append("round")
    sb.append('=')
    sb.append(if (this.round == null) "<null>"
    else this.round)
    sb.append(',')
    sb.append('\n')
    sb.append("\t")
    sb.append("showNumber")
    sb.append('=')
    sb.append(if (this.showNumber == null) "<null>"
    else this.showNumber)
    sb.append(',')
    sb.append('\n')
    sb.append("\t")
    sb.append("additionalProperties")
    sb.append('=')
    sb.append(this.additionalProperties)
    sb.append(',')
    sb.append('\n')
    if (sb.charAt(sb.length - 1) == ',') sb.setCharAt(sb.length - 1, ']')
    else sb.append('}')
    sb.toString
  }
}

