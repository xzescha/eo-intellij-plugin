// SPDX-FileCopyrightText: Copyright (c) 2021-2025 Stepan Strunkov
// SPDX-License-Identifier: MIT

/*
 * SPDX-FileCopyrightText: Copyright (c) 2021-2025 Stepan Strunkov
 * SPDX-License-Identifier: MIT
 */

package org.eolang.jetbrains;

import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.ExternalAnnotator;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import java.util.ArrayList;
import java.util.List;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

//@checkstyle JavadocTagsCheck (9 lines)

/**
 * An external annotator is an object that analyzes code in a document and annotates the PSI
 * elements with errors or warnings. Because such analysis can be expensive, we don't want it in the
 * GUI event loop. Jetbrains provides this external annotator mechanism to run these analyzers out
 * of band.
 */
@SuppressWarnings("PMD.SystemPrintln")
public class EoExternalAnnotator
    extends ExternalAnnotator<PsiFile, List<EoExternalAnnotator.Issue>> {

    // Called first; in our case, just return file and do nothing.
    @Override
    public final @NotNull PsiFile collectInformation(@NotNull final PsiFile file) {
        return file;
    }

    /*
     * Called 2nd; look for trouble in file and return list of issues.
     *
     * <p>For most custom languages, you would not reimplement your semantic analyzer
     * using PSI trees. Instead, here is where you would call out to your custom languages
     * compiler or interpreter to get error messages or other bits of information you'd
     * like to annotate the document with.
     */
    @Contract(value = "_ -> new", pure = true)
    @Override
    public final @NotNull List<Issue> doAnnotate(final PsiFile file) {
        return new ArrayList<>(0);
    }

    // Called 3rd to actually annotate the editor window.

    @Override
    public final void apply(@NotNull final PsiFile file, final @NotNull List<Issue> issues,
        @NotNull final AnnotationHolder holder) {
        for (final Issue issue : issues) {
            final TextRange range = issue.getOffendnode().getTextRange();
            if (range.getStartOffset() > range.getEndOffset()) {
                System.err.println("Invalid TextRange");
                continue;
            }
            holder.createErrorAnnotation(range, issue.getMsg());
        }
    }

    /**
     * Issue report.
     * @since 0.0.0
     */
    public static class Issue {

        /**
         * Message.
         */
        private final String msg;

        /**
         * Node.
         */
        private final PsiElement offendnode;

        /**
         * Issue init.
         * @param msg String
         * @param node PsiElement
         */
        public Issue(final String msg, final PsiElement node) {
            this.msg = msg;
            this.offendnode = node;
        }

        /**
         * Accessor.
         * @return String message
         */
        final String getMsg() {
            return this.msg;
        }

        /**
         * Accessor.
         * @return Offending node
         */
        final PsiElement getOffendnode() {
            return this.offendnode;
        }
    }
}
