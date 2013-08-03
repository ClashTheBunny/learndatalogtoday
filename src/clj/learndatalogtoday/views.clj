(ns learndatalogtoday.views
  (:require [hiccup.page :refer [html5 include-js include-css]]
            [hiccup.element :refer [javascript-tag]]
            [markdown.core :as md]
            [datomic-query-helpers.core :refer [pretty-query-string]]
            [fipp.edn :as fipp]))

(defn footer []
  [:footer.text-center {:style "border-top: 1px solid lightgrey; margin-top: 40px;padding:10px;"}
   [:small
    [:p [:a {:href "#"} "learndatalogtoday.org"] 
     " &copy; 2013 Jonas Enlund"]
    [:p 
     [:a {:href "#"} "github"] " | " 
     [:a {:href "#"} "lispinsummerprojects.org"] " | " 
     [:a {:href "#"} "about"] " | "
     [:a {:href "#"} "license"]]]])

(defn row [& content]
  [:div.row
   [:div.offset2.span8
    content]])

(defn base [chapter text exercises ecount]
  [:html
   [:head
    (include-css "/third-party/bootstrap/css/bootstrap.css")
    (include-css "/third-party/codemirror-3.15/lib/codemirror.css")
    (include-css "/style.css")
    [:title "Learn Datalog Today!"]]
   [:body
    [:div.container
     (row [:div.textcontent text])
     (row (when (> chapter 0) 
            [:a {:href (str "/chapter/" (dec chapter))} 
             "<< Previous chapter"])
          (when (< chapter 9) 
            [:a.pull-right {:href (str "/chapter/" (inc chapter))} 
             "Next chapter >>"]))
     (row [:div.exercises {:style "margin-top: 14px"} exercises])
     (row (footer))]
    (include-js "/third-party/jquery/jquery-1.10.1.min.js")
    (include-js "/third-party/codemirror-3.15/lib/codemirror.js")
    (include-js "/third-party/codemirror-3.15/mode/clojure/clojure.js")
    (include-js "/third-party/bootstrap/js/bootstrap.js")
    (include-js "/app.js")
    (javascript-tag (format "learndatalogtoday.core.init(%s, %s);" chapter ecount))]])

(defn build-input [tab-n input-n input]
  (let [label (condp = (:type input)
                :query "Query:"
                :rule "Rules:"
                :value (str "Input #" input-n ":"))]
    [:div.span8
     [:div.row
      [:div.span8 [:p [:small [:strong label]]]]]
     [:div.row
      [:div.span8 [:textarea {:class (str "input-" tab-n)} (if (= (:type input) :query) 
                                                             (pretty-query-string (:value input))
                                                             ;; TODO pretty-print
                                                             (with-out-str
                                                               (fipp/pprint (:value input))))]]]]))

(defn build-inputs [tab-n inputs]
  (map-indexed (partial build-input tab-n) inputs))

(defn build-exercise [tab-n exercise]
  (list [:div {:class (if (zero? tab-n) "tab-pane active" "tab-pane") 
               :id (str "tab" tab-n)}
         (md/md-to-html-string (:question exercise))
         [:div.row.inputs
          (build-inputs tab-n (:inputs exercise))]
         [:div.row
          [:div.span8
           [:button.btn.btn-block {:id (str "run-query-" tab-n) 
                                   :data-tab tab-n} 
            "Run Query"]]]
         [:div.row
          [:div.span8
           [:div.alerts]
           [:table.table.table-striped.resultset
            [:thead]
            [:tbody]]]]]))

(defn build-exercises [exercises]
  (list [:div.tabbable
         [:ul.nav.nav-tabs
          (for [n (range (count exercises))]
            [:li (when (zero? n) {:class "active"}) 
             [:a {:href (str "#tab" n) 
                  :data-toggle "tab"}
              [:span.label n]]])]
         [:div.tab-content
          (map-indexed build-exercise exercises)]]))

(defn chapter-response [chapter-data]
  (let [text (-> chapter-data :text-file slurp md/md-to-html-string)
        exercises (build-exercises (:exercises chapter-data))
        ecount (count (:exercises chapter-data))
        chapter (:chapter chapter-data)]
    (html5 (base chapter text exercises ecount))))

(defn toc []
  (html5
   [:html
    [:head
     (include-css "/third-party/bootstrap/css/bootstrap.css")
     (include-css "/style.css")
     [:title "Learn Datalog Today!"]]
    [:body
     [:div.container
      (row [:div.textcontent 
            [:h1 "Learn Datalog Today!"]
            [:p "<strong>Learn Datalog Today</strong> is an interactive tutorial designed to teach you the <a href=\"\">datomic</a>  dialect of <a href=\"\">datalog</a>. Datalog is a declarative <strong>database query language</strong> with roots in logic programming. Datalog has similar expressive power as <a href=\"\">SQL</a>."]
            [:p "Datomic is a new database with an interesting and novel architecture giving its users a unique set of features. You can read more about datomic at <a href=\"http://datomic.com\">http://datomic.com</a> and the architecture is described in some detail <a href=\"http://infoq.com\">in this infoq article</a>."]
            [:h2 "Table of Content"]
            [:ul
             [:li [:a {:href "/chapter/0"} "Extensible Data Notation"]]
             [:li [:a {:href "/chapter/1"} "Basic Queries"]]
             [:li [:a {:href "/chapter/2"} "Data Patterns"]]
             [:li [:a {:href "/chapter/3"} "Parameterized queries"]]
             [:li [:a {:href "/chapter/4"} "More Queries"]]
             [:li [:a {:href "/chapter/5"} "Predicates"]]
             [:li [:a {:href "/chapter/6"} "Transformation Functions"]]
             [:li [:a {:href "/chapter/7"} "Aggregates"]]
             [:li [:a {:href "/chapter/8"} "Rules"]]]])
      (row (footer))]
     (include-js "/third-party/jquery/jquery-1.10.1.min.js")
     (include-js "/third-party/bootstrap/js/bootstrap.js")]]))