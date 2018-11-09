package me.david.paintshop;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toSet;
import static me.david.paintshop.PaintFinish.M;

/**
 * Implementation of a <code>PaintShopSolver</code> using "Arc Consistency Algorithm #3"
 * algorithm.
 *
 * @see <a href="https://en.wikipedia.org/wiki/AC-3_algorithm">AC-3 algorithm</a>
 */
public class AC3SatSolver implements PaintShopSolver {
    private static final Logger LOGGER = LoggerFactory.getLogger(AC3SatSolver.class);

    private final int nbPaints;
    private final List<CustomerTaste> customerTastes; //not sorted for this SatSOlver

    public AC3SatSolver(int nbPaints, List<CustomerTaste> customerTastes) {
        this.nbPaints = nbPaints;

        this.customerTastes = customerTastes;
        LOGGER.debug("Cust tastes: {}", this.customerTastes);
    }

    @Override
    public List<String> solutions() {
        Map<Integer, EnumSet<PaintFinish>> domains = initialSearchSpace();
        boolean res = ac3(domains);
        LOGGER.debug("AC3SatSolver - res: {}", res);

        return combine(nbPaints, domains);
    }


    /**
     * AC3 algorithm implementation
     *
     * @param domains <code>A set of domains D(x) for each variable x in X. D(x) contains vx0, vx1... vxn, the possible values of x</code>
     * @return <code>false</code> is unsatisfiable, else <code>true</code>
     */
    private boolean ac3(Map<Integer, EnumSet<PaintFinish>> domains) {
        //an arc will be an int[2]

        //go through customer tastes with only one taste and reduce immediately the csp
        for (CustomerTaste customerTaste : customerTastes) {
            Set<PaintReference> paintReferences = customerTaste.paintReferences();
            if (paintReferences.size() == 1) {
                PaintReference uniquePaintReference = paintReferences.iterator().next();
                domains.put(uniquePaintReference.index(), EnumSet.of(uniquePaintReference.finish()));
            }
        }
        LOGGER.debug("domains = {}", domains);

        //neighbours of a color -> customer tastes referring to this colour
        Map<Integer, Set<CustomerTaste>> neighbors = new HashMap<>(); //neighbours for a paint index. a.k.a 'peers'

        for (CustomerTaste customerTaste : customerTastes) {
            Set<PaintReference> paintReferences = customerTaste.paintReferences();
            for (PaintReference paintReference : paintReferences) {
                Set<CustomerTaste> neighbor = neighbors.getOrDefault(paintReference.index(), new HashSet<>());
                neighbor.add(customerTaste);
                neighbors.put(paintReference.index(), neighbor);
            }
        }
        LOGGER.debug("neighbors = {}" + neighbors);


        Queue<int[]> allPaintIndicesArcs = arcsFromCustomerTastes();
        while (!allPaintIndicesArcs.isEmpty()) {
            int[] arc = allPaintIndicesArcs.remove();
            int xi = arc[0];
            int xj = arc[1];

            if (this.revise(domains, neighbors, xi)) {//if we change the domains, we need to recheck the arcs
                Set<PaintFinish> domainForI = domains.get(xi);
                if (domainForI.isEmpty()) {
                    return false;
                }
                Set<Integer> allOtherVariables = new HashSet<>(); //all other paints related to this paint index through customer tastes
                for (CustomerTaste customerTaste : customerTastes) {
                    Set<Integer> allPaintIndexesReferencesByCustomerTaste = customerTaste.paintReferences()
                            .stream()
                            .map(paintReference -> paintReference.index())
                            .collect(toSet());
                    if (allPaintIndexesReferencesByCustomerTaste.contains(xi)) {
                        allOtherVariables.addAll(allPaintIndexesReferencesByCustomerTaste);
                    }
                }
                for (Integer otherIndex : allOtherVariables) {
                    int[] arcToRevisit = {xi, otherIndex};
                    allPaintIndicesArcs.add(arcToRevisit);
                }
            }
        }
        return true;
    }

    /**
     * Creates a queue of arcs from the customer tastes.
     * <p>An arc is a simple int array of size 2 with Xi,Xj, indices of 2 paints having a
     * constraint between each other.</p>
     *
     * @return the <code>queue</code> of arcs
     */
    private Queue<int[]> arcsFromCustomerTastes() {
        Queue<int[]> allPaintIndicesArcs = new LinkedList<>();
        for (int index = 1; index <= nbPaints; index++) {
            Set<Integer> allOtherVariables = new HashSet<>(); //all other paints related to this paint index through customer tastes
            for (CustomerTaste customerTaste : customerTastes) {
                Set<Integer> allPaintIndexesReferencesByCustomerTaste = customerTaste.paintReferences()
                        .stream()
                        .map(paintReference -> paintReference.index())
                        .collect(toSet());
                if (allPaintIndexesReferencesByCustomerTaste.contains(index)) {
                    allOtherVariables.addAll(allPaintIndexesReferencesByCustomerTaste);
                }
            }

            for (Integer otherIndex : allOtherVariables) {
                int[] arc = {index, otherIndex};
                allPaintIndicesArcs.add(arc);
            }
        }
        return allPaintIndicesArcs;
    }


    private boolean revise(Map<Integer, EnumSet<PaintFinish>> csp,
                           Map<Integer, Set<CustomerTaste>> neighbors,
                           int xi) {
        boolean revised = false;

        Set<PaintFinish> domainI = csp.get(xi); //Di: domain for Xi, a Set<PaintFinish>
        for (PaintFinish x : domainI) {
            if (!accept(xi, x, neighbors, csp)) {
                domainI.remove(x);
                revised = true;
            }
        }
        return revised;
    }

    /**
     * check that setting paint x1 to 'finish' doesn't falsify other customer tastes
     *
     * @param x1        index of the current paint Xi
     * @param finish    finish of the current paint being considered
     * @param neighbors map of customer tastes that include current paint 'x1' being considered
     * @return false if customerTastes are falsified
     */
    private boolean accept(int x1,
                           PaintFinish finish,
                           Map<Integer, Set<CustomerTaste>> neighbors,
                           Map<Integer, EnumSet<PaintFinish>> csp) {
        Set<CustomerTaste> customerTastes = neighbors.get(x1);
        for (CustomerTaste customerTaste : customerTastes) {
            boolean satisfied = customerTaste.paintReferences()
                    .stream()
                    .anyMatch(ref -> (ref.index() == x1 ?
                            ref.finish().equals(finish) :
                            csp.get(ref.index()).contains(ref.finish())));
            if (!satisfied) {
                return false;
            }
        }
        return true;
    }


    /**
     * Generates an initial search space.
     * Paint index is 1-based
     *
     * @return the initial search space (Paint index -> "GM")
     */
    Map<Integer, EnumSet<PaintFinish>> initialSearchSpace() {
        Map<Integer, EnumSet<PaintFinish>> searchspace = new HashMap<>();
        for (int index = 1; index < (this.nbPaints + 1); index++) {
            searchspace.put(index, EnumSet.of(PaintFinish.G, M));
        }
        return searchspace;
    }


    private List<String> combine(int index, Map<Integer, EnumSet<PaintFinish>> csp) {
        if (index == 0) {
            return Collections.singletonList("");
        } else {
            List<String> recur = combine(index - 1, csp);
            List<String> current = csp.get(index)
                    .stream()
                    .map(PaintFinish::name)
                    .collect(Collectors.toList());
            List<String> res = new LinkedList<>();
            for (String c : current) {
                for (String pf : recur) {
                    res.add(pf + c);
                }
            }
            return res;
        }
    }
}
