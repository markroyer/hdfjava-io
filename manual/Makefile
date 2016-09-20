NAME=hdfjavaio-manual
BIB=$(NAME).bib
STYLE=lstcustom.sty

default: pdf

dvi: $(NAME).dvi

$(NAME).dvi: $(NAME).tex $(STYLE) $(BIB)
	latex $(NAME).tex
	bibtex $(NAME)
	latex $(NAME).tex
	# Run latex twice for references
	latex $(NAME).tex

pdf: $(NAME).pdf

$(NAME).pdf: $(NAME).tex $(STYLE) $(BIB)
	pdflatex $(NAME)
	bibtex $(NAME)
	pdflatex $(NAME)
	# Run latex twice for references
	pdflatex $(NAME)

clean:
	rm -f *~ $(addprefix $(NAME).,aux dvi log pdf out blg bbl toc url)

