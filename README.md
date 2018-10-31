# Programming challenge - The Paint Shop (a.k.a Constraint Propragation Problem).

This project is an answer to the 'Paint Shop' programming challenge.

```
mvn package -DskipTests=true
cd target
java -jar paint-shop-1.0-SNAPSHOT.jar src/test/resources/example1.txt
```

## Definition of the problem

> You run a paint shop, and there are a few different colors of paint 
you can prepare. Each color can be either "gloss" (G) or "matte" (M).
>
> You have a number of customers, and each have some colors they like, either gloss or matte. No customer will like more than one color in matte.
>
> You want to mix the colors, so that:
> * There is just one batch for each color, and it's either gloss or matte.
> * For each customer, there is at least one color they like.
> * You make as few mattes as possible (because they are more expensive).
>
> Your program should accept an input file as a command line argument, and print a result to standard out.
> An example input file is:
> ```
> 5
> 1 M 3 G 5 G
> 2 G 3 M 4 G
> 5 M
> ```
>
> The first line specifies how many colors there are.
>
> Each subsequent line describes a customer.  For example, the first customer likes color 1 in matte, color 3 in gloss and color 5 in gloss.
>
> Your program should read an input file like this, and print out either that it is impossible to satisfy all the customer, or describe, for each of the colors, whether it should be made gloss or matte.
>
> The output for the above file should be:
> `G G G G M`
>
> ...because all customers can be made happy by every paint being prepared as gloss except number 5

## Analysis of the problem

This is an example of a (Constraint Satisfiability Problem)[https://en.wikipedia.org/wiki/Constraint_satisfaction_problem].
```
X=\{X_{1},\ldots ,X_{n}\} is a set of variables,
{\displaystyle D=\{D_{1},\ldots ,D_{n}\}} D=\{D_{1},\ldots ,D_{n}\} is a set of the respective domains of values, and
{\displaystyle C=\{C_{1},\ldots ,C_{m}\}} C=\{C_{1},\ldots ,C_{m}\} is a set of constraints.
```

```
```

It could be defined as following.

Let us call X1, X2, .., Xn the paints 1, 2, ..., n in Gloss finish,
and ¬X1, ¬X2, ..., ¬Xn the paints 1, 2, ..., n in Matte finish.
The example above in the definition of the problem could be defined as:

```
n = 5
(¬X1 ∨ X3 ∨ X5) ∧ (X2 ∨ ¬X3 ∨ X4) ∧ (¬X5)
```


## Getting Started

These instructions will get you a copy of the project up and running on your local machine for development and testing purposes. See deployment for notes on how to deploy the project on a live system.

### Prerequisites

What things you need to install the software and how to install them

```
Give examples
```

### Installing

A step by step series of examples that tell you how to get a development env running

Say what the step will be

```
Give the example
```

And repeat

```
until finished
```

End with an example of getting some data out of the system or using it for a little demo

## Running the tests

You can just run the suite of tests using `mvn clean test`.

### Break down into end to end tests

Explain what these tests test and why

```
Give an example
```

### And coding style tests

Explain what these tests test and why

```
Give an example
```

## Building and Running

```
mvn package
java -jar target/paint-shop-1.0-SNAPSHOT.jar src/test/resources/example1.txt

```


## Deployment

Add additional notes about how to deploy this on a live system

## Built With

* [Maven](https://maven.apache.org/) - Dependency Management


## Authors

* **David Sanches** - *Initial work*

## License

This project is licensed under the MIT License - see the [LICENSE.md](LICENSE.md) file for details

## Acknowledgments

* Wikipedia - Constraint satisfaction problem [https://en.wikipedia.org/wiki/Constraint_satisfaction_problem]
* [Peter Norvig](http://norvig.com/), [Artificial Intelligence: A Modern Approach](http://aima.cs.berkeley.edu/)

