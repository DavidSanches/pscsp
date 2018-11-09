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
 * @See https://en.wikipedia.org/wiki/AC-3_algorithm
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
        Map<Integer, EnumSet<PaintFinish>> csp = initialSearchSpace();
        boolean res = ac3(csp);
        LOGGER.debug("AC3SatSolver - res: {}", res);

        return combine(nbPaints, csp);
    }


    private boolean ac3(Map<Integer, EnumSet<PaintFinish>> csp) {
        //an arc will be an int[2]

        //go through customer tastes with only one taste and reduce immediately the csp
        for (CustomerTaste customerTaste : customerTastes) {
            Set<PaintReference> paintReferences = customerTaste.paintReferences();
            if (paintReferences.size() == 1) {
                PaintReference uniquePaintReference = paintReferences.iterator().next();
                csp.put(uniquePaintReference.index(), EnumSet.of(uniquePaintReference.finish()));
            }
        }
        LOGGER.debug("csp = {}", csp);

        //neighbours of a color -> customer tastes referring to this colour
        Map<Integer, Set<CustomerTaste>> neighbors = new HashMap(); //neighbours for a paint index. a.k.a 'peers'

        for (CustomerTaste customerTaste : customerTastes) {
            Set<PaintReference> paintReferences = customerTaste.paintReferences();
            for (PaintReference paintReference : paintReferences) {
                Set<CustomerTaste> neighbor = neighbors.getOrDefault(paintReference.index(), new HashSet<>());
                neighbor.add(customerTaste);
                neighbors.put(paintReference.index(), neighbor);
            }
        }
        LOGGER.debug("neighbors = {}" + neighbors);


        Queue<int[]> allPaintIndicesArcs = new LinkedList();
        for (int index = 1; index <= nbPaints; index++) {
            Set<Integer> allOtherVariables = new HashSet(); //all other paints related to this paint index through customer tastes
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

        while (!allPaintIndicesArcs.isEmpty()) {
            int[] arc = allPaintIndicesArcs.remove();
            int xi = arc[0];
            int xj = arc[1];

            if (this.revise(csp, neighbors, xi, xj)) {//if we change the csp, we need to recheck the arcs
                Set domainForI = csp.get(xi);
                if (domainForI.isEmpty()) {
                    return false;
                }
                Set<Integer> allOtherVariables = new HashSet(); //all other paints related to this paint index through customer tastes
                for (CustomerTaste customerTaste : customerTastes) {
                    Set<Integer> allPaintIndexesReferencesByCustomerTaste = customerTaste.paintReferences()
                            .stream()
                            .map(paintReference -> paintReference.index())
                            .collect(toSet());
                    if (allPaintIndexesReferencesByCustomerTaste.contains(xi)) {
                        allOtherVariables.addAll(allPaintIndexesReferencesByCustomerTaste);
                    }
                }
            }
        }
        return true;
    }


    private boolean revise(Map<Integer, EnumSet<PaintFinish>> csp,
                           Map<Integer, Set<CustomerTaste>> neighbors,
                           int xi, int xj) {
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
//
//    /**
//     * need to make sure that setting x for paint x1 is ok for other customer tastes
//     * @param x1
//     * @param x
//     * @param domainJ
//     * @return
//     */
//    private boolean djAccept(int x1,
//                             PaintFinish x,
//                             Map<Integer, Set<CustomerTaste>> neighbors) {
//        Set<CustomerTaste> customerAffectedByX1Tox = neighbors.get(x1);
//        for (CustomerTaste customerTaste : customerAffectedByX1Tox) {
//            if (!customerTaste.likes(x1, x)) {
//                return false;
//            }
//        }
//        return true;
//    }


    /**
     * Generates an initial search space.
     * Paint index is 1-based
     *
     * @return the initial search space (Paint index -> "GM")
     */
    Map<Integer, EnumSet<PaintFinish>> initialSearchSpace() {
        Map<Integer, EnumSet<PaintFinish>> searchspace = new HashMap();
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
