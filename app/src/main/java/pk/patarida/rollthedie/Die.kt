package pk.patarida.rollthedie

class Die(
    sides: Int=6,       // // constructor for 0,1 parameters, max side
){
    // properties (data member)
    private var name = "d"+sides
    private var sides = sides
    private var currentSide: Int

    // define the currentSide
    init {
        this.currentSide = kotlin.random.Random.nextInt(from =1 ,until = this.sides+1)
    }

    // constructor for 2 parameters, name and side
    constructor(
        name:String,
        sides:Int,
    ): this(sides){
        this.name = name
        this.sides = sides
    }

    // method for this class
    fun getName(): String = this.name
    fun getSides(): Int = this.sides
    fun setCurrentSide(number: Int): Unit { this.currentSide = number}  // check that it is not > sides
    fun getCurrentSide(): Int = this.currentSide
    fun roll(): Int {
        this.currentSide = kotlin.random.Random.nextInt(from =1 ,until = this.sides+1)
        return this.currentSide
    }

}

