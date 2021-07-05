# Neuroevolution of Augmenting Topologies

This is a java library for the NEAT algorithm.

It implements all processes of evolution described in Stanley and Miikkulainen's Evolving Neural 
Networks through Augmenting Topologies (2002), while adding some functionality not in the paper. 

It supports recursive connections, custom activation functions, complete parameterization of 
behaviour, multiple instances using different parameters, guaranteed equal results using random 
instances with the same seed, saving and loading genomes, etc.

## Examples

Check out the included [examples](https://github.com/tesladodger/NEAT2/tree/master/lib/src/test/java/demos)
to learn how to use this library.

The [EvolveXOR](https://github.com/tesladodger/NEAT2/blob/master/lib/src/test/java/demos/EvolveXOR.java)
example is the simplest. A slightly more complex demo is 
[EvolvePoleBalancingNV](https://github.com/tesladodger/NEAT2/blob/master/lib/src/test/java/demos/EvolvePoleBalancingNV.java).

The [Drift2](https://github.com/tesladodger/NEAT2/tree/master/lib/src/test/java/demos/drift2) 
example, using Processing, uses this library to evolve a group of cars to lap around a track.

The javadoc is extensive, and the package-info explains intended usage.
